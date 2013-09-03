/*  
 * Copyright 2012-2013 xavi.ferro
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
package shisha.statemachine;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;

import shisha.statemachine.annotations.AStateMachine;
import shisha.statemachine.annotations.EnterState;
import shisha.statemachine.annotations.Event;
import shisha.statemachine.annotations.ExitState;
import shisha.statemachine.annotations.State;
import shisha.statemachine.annotations.Transition;
import shisha.statemachine.annotations.Transitions;
import shisha.statemachine.exceptions.EventAlreadyExistsException;
import shisha.statemachine.exceptions.IllegalControllerAnnotationException;
import shisha.statemachine.exceptions.IllegalEventAnnotationException;
import shisha.statemachine.exceptions.IllegalStateAnnotationException;
import shisha.statemachine.exceptions.IllegalTransitionAnnotationException;
import shisha.statemachine.exceptions.StateMachineDefinitionException;
import shisha.statemachine.strategy.NonReentrantStrategy;
import shisha.statemachine.strategy.ReentrantStrategy;

/**
 * Helper class for creating state machines from a state machine definition or
 * from an annotated class.
 * 
 * <p>
 * The annotated class must be annotated with {@link AStateMachine}
 */
public class StateMachines {
    protected static Logger l = getLogger(StateMachines.class);

    public static StateMachine newReentrant(StateMachineDefinition definition) throws StateMachineDefinitionException {
        return new StateMachineImpl(definition, new ReentrantStrategy());
    }
    
    public static StateMachine newReentrant(Object instance) throws StateMachineDefinitionException {
        return new StateMachineImpl(processAnnotatedController(instance), new ReentrantStrategy());
    }

    public static StateMachine newNonReentrant(StateMachineDefinition definition) throws StateMachineDefinitionException {
        return new StateMachineImpl(definition, new NonReentrantStrategy());
    }
    
    public static StateMachine newNonReentrant(Object instance) throws StateMachineDefinitionException {
        return new StateMachineImpl(processAnnotatedController(instance), new NonReentrantStrategy());
    }

    static void checkClassAnnotation(StateMachineDefinition definition, Object instance)
            throws StateMachineDefinitionException {
        Class<?> clazz = instance.getClass();
        if (!clazz.isAnnotationPresent(shisha.statemachine.annotations.AStateMachine.class)) {
            throw new IllegalControllerAnnotationException(
                    "All state machines must be annotated with the @AStateMachine annotation");
        }
    }

    static StateMachineDefinitionImpl checkFieldAnnotations(StateMachineDefinitionImpl stateMachineDefinition,
            Object instance) throws StateMachineDefinitionException {
        Class<?> clazz = instance.getClass();

        // Let's process the events and states first.
        // We look for the State, StartState and Event annotations
        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(State.class))
                checkStateAnnotation(instance, stateMachineDefinition, field, field.getAnnotation(State.class));

            if (field.isAnnotationPresent(Event.class))
                checkEventAnnotation(instance, stateMachineDefinition, field, field.getAnnotation(Event.class));
        }

        return stateMachineDefinition;
    }

    static private StateMachineDefinition processAnnotatedController(Object instance)
            throws StateMachineDefinitionException {
        StateMachineDefinitionImpl stateMachineDefinition = new StateMachineDefinitionImpl();

        checkClassAnnotation(stateMachineDefinition, instance);
        checkFieldAnnotations(stateMachineDefinition, instance);
        checkTransitionAnnotations(stateMachineDefinition, instance);

        return stateMachineDefinition;
    }

    static void checkTransitionAnnotations(StateMachineDefinitionImpl definition, Object instance)
            throws StateMachineDefinitionException {
        // Let's process the transitions
        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Transitions.class)) {
                Transitions transitions = method.getAnnotation(Transitions.class);
                for (Transition transition : transitions.value())
                    checkTransitionAnnotation(instance, definition, method, transition);
            } else if (method.isAnnotationPresent(Transition.class)) {
                checkTransitionAnnotation(instance, definition, method, method.getAnnotation(Transition.class));
            } else if (method.isAnnotationPresent(EnterState.class)) {
                checkEnterStateAnnotation(instance, definition, method, method.getAnnotation(EnterState.class));
            } else if (method.isAnnotationPresent(ExitState.class)) {
                checkExitStateAnnotation(instance, definition, method, method.getAnnotation(ExitState.class));
            }
        }
    }

    static void checkGenericTransitionHasTheRightParameters(Method method) throws IllegalTransitionAnnotationException {
        Class<?> paramTypes[] = method.getParameterTypes();
        if (paramTypes == null || paramTypes.length != 1 || !paramTypes[0].equals(TransitionInfo.class))
            throw new IllegalTransitionAnnotationException("Transition for method " + method.getName()
                    + " is not well defined. It should have one and only TransitionEvent paramter");
    }

    static void checkEnterStateAnnotation(Object instance, StateMachineDefinitionImpl definition, Method method,
            EnterState ann) throws StateMachineDefinitionException {
        // First of all, we check the parameters
        checkGenericTransitionHasTheRightParameters(method);

        // Second, we check the return type is correct
        Class<?> resultType = method.getReturnType();
        if (resultType == null || !resultType.equals(EventInfo.class))
            throw new IllegalTransitionAnnotationException("Transition for method " + method.getName()
                    + " is not well defined. Enter phase must return a EventInfo");

        definition.defineEnterState(ann, method, instance);
    }

    static void checkExitStateAnnotation(Object instance, StateMachineDefinitionImpl definition, Method method,
            ExitState ann) throws StateMachineDefinitionException {
        // First of all, we check the parameters
        checkGenericTransitionHasTheRightParameters(method);
        // Second, we check the return type is correct
        Class<?> resultType = method.getReturnType();
        if (resultType == null || !resultType.equals(Boolean.class))
            throw new IllegalTransitionAnnotationException("Transition for method " + method.getName()
                    + " is not well defined. Exit phase must return a boolean");
        
        definition.defineExitState(ann, method, instance);
    }

    static private void checkStateAnnotation(Object instance, StateMachineDefinitionImpl definition, Field field,
            State ann) throws IllegalStateAnnotationException, StateMachineDefinitionException {
        if (!isStringAndFinal(field))
            throw new IllegalStateAnnotationException("@State " + field.getName()
                    + " must be declared as public static final");

        try {
            String stateName = (String) field.get(instance);
            definition.defineState(stateName, ann.isStart(), ann.isFinal());
        } catch (IllegalAccessException e) {
            l.error("Error. This should never happen as we have checked the conditions before using reflection", e);
        }
    }

    static private void checkEventAnnotation(Object instance, StateMachineDefinitionImpl definition, Field field,
            Event ann) throws IllegalEventAnnotationException {
        if (!isStringAndFinal(field))
            throw new IllegalEventAnnotationException("@Event " + field.getName()
                    + " must be declared as public static final");

        try {
            String eventName = (String) field.get(instance);
            definition.defineEvent(eventName);
        } catch (IllegalAccessException e) {
            l.error("ERROR. This should never happen as we have checked the conditions before using reflection", e);
        } catch (EventAlreadyExistsException e) {
            throw new IllegalEventAnnotationException("@Event " + field.getName() + " has been declared twice");
        }
    }

    /*
     * TODO. Define a set of tests for this functionality
     */
    static private void checkTransitionAnnotation(Object instance, StateMachineDefinitionImpl stateMachineDefinition,
            Method method, Transition ann) throws StateMachineDefinitionException, IllegalTransitionAnnotationException {
        // First of all, we check the parameters
        checkGenericTransitionHasTheRightParameters(method);
        stateMachineDefinition.defineTransition(ann, method, instance);
    }

    /**
     * We check that the annotated field is a public final String type
     * 
     * @return true if it conforms the condition, false otherwise.
     */
    static private boolean isStringAndFinal(Field field) {
        return (field.getType().equals(String.class) && Modifier.isFinal(field.getModifiers()) && Modifier
                .isPublic(field.getModifiers()));
    }

}
