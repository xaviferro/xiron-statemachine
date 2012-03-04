/*  
 * Copyright 2012 xavi.ferro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */   
package net.xiron.pattern.statemachine.annotated;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.xiron.pattern.statemachine.PhaseEnterResult;
import net.xiron.pattern.statemachine.StateMachine;
import net.xiron.pattern.statemachine.StateMachineController;
import net.xiron.pattern.statemachine.StateMachineImpl;
import net.xiron.pattern.statemachine.TransitionEvent;
import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.IllegalAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalEventAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalStateAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalTransitionAnnotationException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for processing annotated controllers.
 * 
 * TODO. A fancy improvement would be to support more than one annotated processor 
 * at a time.
 * 
 * @author xavi.ferro
 */
public class AnnotatedControllerProcessor implements StateMachineController {
    private static Logger l = LoggerFactory.getLogger(AnnotatedControllerProcessor.class);
    
    private StateMachine stateMachine;
    private AnnotatedController realController;
    
    private TransitionDictionary transitionDictionary;
    
    public AnnotatedControllerProcessor(AnnotatedController realController) 
        throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException
    {
        this.stateMachine = new StateMachineImpl();
        this.stateMachine.setController(this);
        
        this.realController = realController;
        this.transitionDictionary = new TransitionDictionary();
        
        processAnnotatedController();
    }
    
    public StateMachine getStateMachine() {
        return this.stateMachine;
    }
    
    /**
     * We check that the annotated field is a public final String type
     * 
     * @return true if it conforms the condition, false otherwise.
     */
    private boolean isStringAndFinal(Field field) {
        return (field.getType().equals(String.class) 
                && Modifier.isFinal(field.getModifiers())
                && Modifier.isPublic(field.getModifiers()));
    }
    
    private void checkStateAnnotation(Field field, State ann) throws IllegalStateAnnotationException {
        if (!isStringAndFinal(field))
            throw new IllegalStateAnnotationException("@State " + field.getName() + " must be declared as public static final");
            
        try {
            String stateName = (String) field.get(this.realController);
            stateMachine.defineState(stateName);
        } catch (IllegalAccessException e) {
            l.error("EERROR", e); 
        }
    }
    
    private void checkEventAnnotation(Field field, Event ann) throws IllegalEventAnnotationException {
        if (!isStringAndFinal(field))
            throw new IllegalEventAnnotationException("@Event " + field.getName() + " must be declared as public static final");
            
        try {
            String eventName = (String) field.get(this.realController);
            stateMachine.defineEvent(eventName);
        } catch (IllegalAccessException e) {
            l.error("EERROR", e);
        } 
    }
    
    private void checkStartStateAnnotation(Field field, StartState ann) 
        throws StateNotDefinedException, IllegalArgumentException 
    {
        if (!isStringAndFinal(field))
            throw new IllegalArgumentException();

        try {
            String startState = (String) field.get(this.realController);
            stateMachine.setStartState(startState);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /*
     * TODO. Define a set of tests for this functionality
     */
    private void checkTransitionAnnotation(Method method, Transition ann) 
        throws StateNotDefinedException, EventNotDefinedException, IllegalTransitionAnnotationException
    {
        // First of all, we check the parameters 
        Class<?> paramTypes[] = method.getParameterTypes();
        if (paramTypes == null || paramTypes.length != 1 || !paramTypes[0].equals(TransitionEvent.class))
            throw new IllegalTransitionAnnotationException("Transition for method " + method.getName() + " is not well defined. It should have one and only TransitionEvent paramter");
        
        // Second, we check the return type is correct
        Class<?> resultType = method.getReturnType();
        if (ann.phase().equals(TransitionPhases.PHASE_EXIT)) {
            if (resultType == null || !resultType.equals(Boolean.class))
                throw new IllegalTransitionAnnotationException("Transition for method " + method.getName() + " is not well defined. Exit phase must return a boolean");
        } else if (ann.phase().equals(TransitionPhases.PHASE_ENTER)) {
            if (resultType == null || !resultType.equals(PhaseEnterResult.class))
                throw new IllegalTransitionAnnotationException("Transition for method " + method.getName() + " is not well defined. Enter phase must return a PhaseEnterResult");
        } 
        
        stateMachine.defineTransition(ann.source(), ann.target(), ann.event());
        transitionDictionary.addTransition(ann, method, this.realController);
    }
    
    private void processAnnotatedController() 
        throws StateNotDefinedException, EventNotDefinedException, IllegalTransitionAnnotationException, IllegalAnnotationException
    {
        Class<? extends AnnotatedController> clazz = realController.getClass();
        
        // Let's process the events and states first.
        // We look for the State, StartState and Event annotations
        for (Field field: clazz.getFields()) {
            if (field.isAnnotationPresent(State.class))
                checkStateAnnotation(field, field.getAnnotation(State.class));
            
            if (field.isAnnotationPresent(Event.class))
                checkEventAnnotation(field, field.getAnnotation(Event.class));
            
            if (field.isAnnotationPresent(StartState.class))
                checkStartStateAnnotation(field, field.getAnnotation(StartState.class));
        }   
        
        // Let's process the transitions 
        for (Method method: clazz.getMethods()) {
            if (!method.isAnnotationPresent(Transitions.class)) {
                if (method.isAnnotationPresent(Transition.class))
                    checkTransitionAnnotation(method, method.getAnnotation(Transition.class));
            } else {
                Transitions transitions = method.getAnnotation(Transitions.class);
                for (Transition transition: transitions.value())
                    checkTransitionAnnotation(method, transition);
            }
        }
    }
    
    /*
     * TODO. Decide if this is necessary or not. Maybe we could expose the state machine itself
     */
    public void processEvent(String event, Object object) 
        throws ReentrantTransitionNotAllowed, StateNotDefinedException, EventNotDefinedException, TransitionNotDefinedException 
    {
        stateMachine.processEvent(event, object);
    }
    
    @Override public boolean phaseExitState(TransitionEvent event) {
        TransitionDefinition def = transitionDictionary.findBy(event.getSource(), 
                                                               event.getTarget(), 
                                                               event.getEvent(), 
                                                               TransitionPhases.PHASE_EXIT);
        
        boolean result = true;
        if (def != null) {
            if (l.isDebugEnabled())
                l.debug("#phaseExitState: found a match " + event.toString());
            result = def.executeExitPhase(event);
        }
        return result;
    }

    @Override public void phaseTransition(TransitionEvent event) {
        TransitionDefinition def = transitionDictionary.findBy(event.getSource(), 
                                                               event.getTarget(), 
                                                               event.getEvent(), 
                                                               TransitionPhases.PHASE_TRANSITION);
        if (def != null) {
            if (l.isDebugEnabled())
                l.debug("#phaseTransition: found a match " + event.toString()); 
            def.executeTransitionPhase(event);
        }
    }

    @Override public PhaseEnterResult phaseEnterState(TransitionEvent event) {
        TransitionDefinition def = transitionDictionary.findBy(event.getSource(), 
                                                               event.getTarget(), 
                                                               event.getEvent(), 
                                                               TransitionPhases.PHASE_ENTER);
        
        PhaseEnterResult result = null;
        if (def != null) {
            if (l.isDebugEnabled())
                l.debug("#phaseEnterState: found a match " + event.toString());
            result = def.executeEnterPhase(event);
        }
        
        return result;
    }
    
    static class TransitionDictionary {
        private ArrayList<TransitionDefinition> transitionDictionary = new ArrayList<TransitionDefinition> ();
        
        public void addTransition(Transition tx, Method m, Object instance) {
            transitionDictionary.add(new TransitionDefinition(tx, m, instance));
        }
        
        public TransitionDefinition findBy(String source,
                                           String target,
                                           String event,
                                           TransitionPhases phase) 
        {
            TransitionDefinition transition = null;
            for (TransitionDefinition def:transitionDictionary) {
                if (def.transition.target().equals(target) &&
                    def.transition.source().equals(source) &&
                    def.transition.event().equals(event) &&
                    def.transition.phase().equals(phase)) 
                {
                    transition = def;
                    break;
                }
            }
            
            return transition;
        }
    }
    
    private static class TransitionDefinition {
        private Method method;
        private Transition transition;
        private Object instance;
        
        TransitionDefinition(Transition transition, Method method, Object instance) {
            this.method = method;
            this.transition = transition;
            this.instance = instance;
        }
        
        public boolean executeExitPhase(TransitionEvent evt) {
            boolean result = true;
            try {
                result = (Boolean) method.invoke(instance, evt);
            } catch (IllegalAccessException e) {
                l.error("#executeExitPhase error. This SHOULD not happen", e);
            } catch (InvocationTargetException e) {
                l.error("#executeExitPhase error. This should never happen", e);
            }
            return result;
        }
        
        /*
         * TODO . Check exceptions. I think we shouldn't do anything with them as
         * only allowed transitions are taken into account.
         */
        public void executeTransitionPhase(TransitionEvent transitionEvent) {
            try {
                method.invoke(instance, transitionEvent);
            } catch (IllegalAccessException e) {
                l.error("#executeTransitionPhase error", e);
            } catch (InvocationTargetException e) {
                l.error("#executeTransitionPhase error. This should never happen", e);
            }
        }
        
        public PhaseEnterResult executeEnterPhase(TransitionEvent event) {
            PhaseEnterResult result = null;
            try {
                result = (PhaseEnterResult) method.invoke(instance, event);
            } catch (IllegalAccessException e) {
                l.error("#executeEnterPhase error. This should never happen", e);
            } catch (InvocationTargetException e) {
                l.error("#executeEnterPhase error. This should never happen", e);
            }
            return result;
        }
    }
}
