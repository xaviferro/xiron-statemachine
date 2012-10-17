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
package net.xiron.pattern.statemachine.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.xiron.pattern.statemachine.EventInfo;
import net.xiron.pattern.statemachine.StateMachine;
import net.xiron.pattern.statemachine.StateMachineDefinition;
import net.xiron.pattern.statemachine.StateMachineDefinitionImpl;
import net.xiron.pattern.statemachine.StateMachineImpl;
import net.xiron.pattern.statemachine.StateMachineStrategy;
import net.xiron.pattern.statemachine.TransitionController;
import net.xiron.pattern.statemachine.TransitionInfo;
import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.IllegalAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalControllerAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalEventAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalStateAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalTransitionAnnotationException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateMachineDefinitionException;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.strategy.NonReentrantStrategy;
import net.xiron.pattern.statemachine.strategy.ReentrantEnqueueStrategy;
import net.xiron.pattern.statemachine.strategy.ReentrantStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for processing annotated controllers.
 * 
 * TODO. A fancy improvement would be to support more than one annotated
 * processor at a time
 */
public class AnnotatedControllerProcessor implements TransitionController {
    private static Logger l = LoggerFactory
            .getLogger(AnnotatedControllerProcessor.class);

    private StateMachine stateMachine;
    private StateMachineDefinition definition;
    private StateMachineStrategy strategy;

    private Object realController;

    private TransitionDictionary transitionDictionary;

    public AnnotatedControllerProcessor(Object realController)
            throws StateMachineDefinitionException, IllegalAnnotationException {
        this.definition = new StateMachineDefinitionImpl();

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
                && Modifier.isFinal(field.getModifiers()) && Modifier
                    .isPublic(field.getModifiers()));
    }

    private net.xiron.pattern.statemachine.annotations.StateMachine checkTypeAnnotation(Object object)
            throws EventNotDefinedException,
            IllegalControllerAnnotationException, IllegalAnnotationException,
            StateNotDefinedException {
        net.xiron.pattern.statemachine.annotations.StateMachine sm = object
                .getClass()
                .getAnnotation(
                        net.xiron.pattern.statemachine.annotations.StateMachine.class);
        if (sm == null)
            throw new IllegalControllerAnnotationException(
                    "Object does not contain any valid controller annotation");

        return sm;
    }

    private void checkStateAnnotation(Field field, State ann)
            throws IllegalStateAnnotationException,
            StateMachineDefinitionException {
        if (!isStringAndFinal(field))
            throw new IllegalStateAnnotationException("@State "
                    + field.getName()
                    + " must be declared as public static final");

        try {
            String stateName = (String) field.get(this.realController);
            definition.defineState(stateName, ann.isStart(), ann.isEnd());
        } catch (IllegalAccessException e) {
            l.error("Error. This should never happen as we have checked the conditions before using reflection",
                    e);
        }
    }

    private void checkEventAnnotation(Field field, Event ann)
            throws IllegalEventAnnotationException {
        if (!isStringAndFinal(field))
            throw new IllegalEventAnnotationException("@Event "
                    + field.getName()
                    + " must be declared as public static final");

        try {
            String eventName = (String) field.get(this.realController);
            definition.defineEvent(eventName);
        } catch (IllegalAccessException e) {
            l.error("ERROR. This should never happen as we have checked the conditions before using reflection",
                    e);
        }
    }

    /*
     * TODO. Define a set of tests for this functionality
     */
    private void checkTransitionAnnotation(Method method, Transition ann)
            throws StateMachineDefinitionException,
            IllegalTransitionAnnotationException {
        // First of all, we check the parameters
        Class<?> paramTypes[] = method.getParameterTypes();
        if (paramTypes == null || paramTypes.length != 1
                || !paramTypes[0].equals(TransitionInfo.class))
            throw new IllegalTransitionAnnotationException(
                    "Transition for method "
                            + method.getName()
                            + " is not well defined. It should have one and only TransitionEvent paramter");

        // Second, we check the return type is correct
        Class<?> resultType = method.getReturnType();
        if (ann.phase().equals(TransitionPhases.PHASE_EXIT)) {
            if (resultType == null || !resultType.equals(Boolean.class))
                throw new IllegalTransitionAnnotationException(
                        "Transition for method "
                                + method.getName()
                                + " is not well defined. Exit phase must return a boolean");
        } else if (ann.phase().equals(TransitionPhases.PHASE_ENTER)) {
            if (resultType == null
                    || !resultType.equals(EventInfo.class))
                throw new IllegalTransitionAnnotationException(
                        "Transition for method "
                                + method.getName()
                                + " is not well defined. Enter phase must return a PhaseEnterResult");
        }

        definition.defineTransition(ann.source(), ann.target(), ann.event());
        transitionDictionary.addTransition(ann, method, this.realController);
    }

    private void processAnnotatedController()
            throws StateMachineDefinitionException,
            IllegalTransitionAnnotationException, IllegalAnnotationException {
        Class<?> clazz = realController.getClass();

        net.xiron.pattern.statemachine.annotations.StateMachine annType = this
                .checkTypeAnnotation(realController);

        // Let's process the events and states first.
        // We look for the State, StartState and Event annotations
        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(State.class))
                checkStateAnnotation(field, field.getAnnotation(State.class));

            if (field.isAnnotationPresent(Event.class))
                checkEventAnnotation(field, field.getAnnotation(Event.class));
        }

        // Let's process the transitions
        for (Method method : clazz.getMethods()) {
            if (!method.isAnnotationPresent(Transitions.class)) {
                if (method.isAnnotationPresent(Transition.class))
                    checkTransitionAnnotation(method,
                            method.getAnnotation(Transition.class));
            } else {
                Transitions transitions = method
                        .getAnnotation(Transitions.class);
                for (Transition transition : transitions.value())
                    checkTransitionAnnotation(method, transition);
            }
        }

        switch (annType.strategy()) {
        case NON_REENTRANT:
            this.strategy = new NonReentrantStrategy();
            break;
        case REENTRANT:
            strategy = new ReentrantStrategy();
            break;
        case ENQUEUE:
            strategy = new ReentrantEnqueueStrategy();
            break;
        }

        this.stateMachine = new StateMachineImpl(this.definition, this.strategy);
    }

    /*
     * TODO. Decide if this is necessary or not. Maybe we could expose the state
     * machine itself
     */
    public void processEvent(String event, Object object)
            throws ReentrantTransitionNotAllowed,
            StateMachineDefinitionException {
        this.stateMachine.processEvent(event, object, this, null);
    }

    @Override
    public boolean exitStatePhase(TransitionInfo event) {
        TransitionDefinition def = transitionDictionary.findBy(
                event.getSource(), event.getTarget(), event.getEvent(),
                TransitionPhases.PHASE_EXIT);

        boolean result = true;
        if (def != null) {
            if (l.isDebugEnabled())
                l.debug("#phaseExitState: found a match " + event.toString());
            result = def.executeExitPhase(event);
        }
        return result;
    }

    @Override
    public void transitionPhase(TransitionInfo event) {
        TransitionDefinition def = transitionDictionary.findBy(
                event.getSource(), event.getTarget(), event.getEvent(),
                TransitionPhases.PHASE_TRANSITION);
        if (def != null) {
            if (l.isDebugEnabled())
                l.debug("#phaseTransition: found a match " + event.toString());
            def.executeTransitionPhase(event);
        }
    }

    @Override
    public EventInfo enterStatePhase(TransitionInfo event) {
        TransitionDefinition def = transitionDictionary.findBy(
                event.getSource(), event.getTarget(), event.getEvent(),
                TransitionPhases.PHASE_ENTER);

        EventInfo result = null;
        if (def != null) {
            if (l.isDebugEnabled())
                l.debug("#phaseEnterState: found a match " + event.toString());
            result = def.executeEnterPhase(event);
        }

        return result;
    }

    static class TransitionDictionary {
        private ArrayList<TransitionDefinition> transitionDictionary = new ArrayList<TransitionDefinition>();

        public void addTransition(Transition tx, Method m, Object instance) {
            transitionDictionary.add(new TransitionDefinition(tx, m, instance));
        }

        public TransitionDefinition findBy(String source, String target,
                                           String event, TransitionPhases phase) {
            TransitionDefinition transition = null;
            for (TransitionDefinition def : transitionDictionary) {
                if (def.transition.target().equals(target)
                        && def.transition.source().equals(source)
                        && def.transition.event().equals(event)
                        && def.transition.phase().equals(phase)) {
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

        TransitionDefinition(Transition transition, Method method,
                Object instance) {
            this.method = method;
            this.transition = transition;
            this.instance = instance;
        }

        public boolean executeExitPhase(TransitionInfo evt) {
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
         * Exceptions at this point are shouldn't be taken into account as we
         * have already checked them when creating them
         */
        public void executeTransitionPhase(TransitionInfo transitionEvent) {
            try {
                method.invoke(instance, transitionEvent);
            } catch (IllegalAccessException e) {
                l.error("#executeTransitionPhase error", e);
            } catch (InvocationTargetException e) {
                l.error("#executeTransitionPhase error. This should never happen",
                        e);
            }
        }

        public EventInfo executeEnterPhase(TransitionInfo event) {
            EventInfo result = null;
            try {
                result = (EventInfo) method.invoke(instance, event);
            } catch (IllegalAccessException e) {
                l.error("#executeEnterPhase error. This should never happen", e);
            } catch (InvocationTargetException e) {
                l.error("#executeEnterPhase error. This should never happen", e);
            }
            return result;
        }
    }
}
