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
package shisha.pattern.statemachine;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import shisha.statemachine.StateMachineDefinition;
import shisha.statemachine.StateMachineDefinitionImpl;
import shisha.statemachine.StateMachineImpl;
import shisha.statemachine.TransitionController;
import shisha.statemachine.exceptions.ConstraintException;
import shisha.statemachine.exceptions.EventAlreadyExistsException;
import shisha.statemachine.exceptions.StateAlreadyExistsException;
import shisha.statemachine.exceptions.StateMachineDefinitionException;
import shisha.statemachine.exceptions.StateMachineException;
import shisha.statemachine.exceptions.StateNotDefinedException;
import shisha.statemachine.exceptions.TransitionNotDefinedException;
import shisha.statemachine.impl.NonReentrantStateMachine;

public class StateMachineDefinitionTest {
    public static String STATE_A = "STATE_A";
    public static String STATE_B = "STATE_B";
    public static String STATE_C = "STATE_C";

    public static String EVENT_AB = "EVENT_AB";
    public static String EVENT_BC = "EVENT_BC";
    public static String EVENT_BB = "EVENT_BB";
    public static String EVENT_BA = "EVENT_BA";
    public static String EVENT_CC = "EVENT_CC";

    private StateMachineDefinitionImpl definition;
    private TransitionController transitionController;

    private StateMachineDefinitionImpl createMachineDefinition() throws StateMachineDefinitionException {
        StateMachineDefinitionImpl definition = new StateMachineDefinitionImpl();
        definition.defineEvent(EVENT_AB);
        definition.defineEvent(EVENT_BC);
        definition.defineEvent(EVENT_BB);
        definition.defineEvent(EVENT_BA);
        definition.defineEvent(EVENT_CC);

        definition.defineState(STATE_A, true, false);
        definition.defineState(STATE_B);
        definition.defineState(STATE_C, false, true);

        definition.defineTransition(STATE_A, EVENT_AB, STATE_B, transitionController);
        definition.defineTransition(STATE_B, EVENT_BC, STATE_C, transitionController);
        definition.defineTransition(STATE_B, EVENT_BB, STATE_B, transitionController);
        definition.defineTransition(STATE_B, EVENT_BA, STATE_A, transitionController);

        return definition;
    }

    @BeforeMethod
    public void beforeAnyMethod() throws StateMachineDefinitionException {
        transitionController = mock(TransitionController.class);
        definition = createMachineDefinition();
    }

    @Test(expectedExceptions = ConstraintException.class)
    public void testDefineStateAsStartAndFinal() throws StateMachineDefinitionException {
        definition.defineState("DOHH", true, true);
    }

    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testAddTransitionToFinalState() throws StateMachineDefinitionException {
        definition.defineTransition(STATE_C, STATE_A, EVENT_AB, transitionController);
    }

    @Test(expectedExceptions = StateAlreadyExistsException.class)
    public void testAddingAStateThatAlreadyExists() throws StateMachineDefinitionException {
        definition.defineState(STATE_A);
    }

    @Test(expectedExceptions = EventAlreadyExistsException.class)
    public void testAddingAnEventThatAlreadyExists() throws StateMachineDefinitionException {
        definition.defineEvent(EVENT_AB);
    }

    @Test
    public void testStartStateAssignedProperly() throws StateMachineDefinitionException {
        assertEquals(STATE_A, definition.getStartState());
    }

    @Test
    public void testTransitionsAreProperlyDefined() throws StateMachineDefinitionException {
        assertEquals(true, definition.isState(STATE_A));
        assertEquals(STATE_B, definition.getTargetState(STATE_A, EVENT_AB));
        assertEquals(1, definition.getApplicableEvents(STATE_A).size());
    }

    @Test
    public void testStatesAreProperlyDefined() throws StateMachineDefinitionException {
        assertEquals(definition.isState(STATE_A), true);
        assertEquals(definition.isStartState(STATE_A), true);
        assertEquals(definition.isFinalState(STATE_A), false);

        assertEquals(definition.isState(STATE_B), true);
        assertEquals(definition.isStartState(STATE_B), false);
        assertEquals(definition.isFinalState(STATE_B), false);

        assertEquals(definition.isState(STATE_C), true);
        assertEquals(definition.isStartState(STATE_C), false);
        assertEquals(definition.isFinalState(STATE_C), true);

        assertEquals(3, definition.getStates().size());
    }

    @Test(expectedExceptions = StateNotDefinedException.class)
    public void testGetTargetStateForNullState() throws StateNotDefinedException, TransitionNotDefinedException {
        definition.getTargetState(null, EVENT_AB);
    }

    @Test
    public void testFinalStatesAreProperlyDefined() {
        List<String> finalStates = definition.getFinalStates();
        assertEquals(definition.getFinalStates().size(), 1);
        assertEquals(finalStates.get(0), STATE_C);
    }

    @Test
    public void testStateIsNotDefined() throws StateMachineDefinitionException {
        assertEquals(false, definition.isState("DOHH"));
    }

    @Test
    public void testEventIsNotDefined() throws StateMachineDefinitionException {
        assertEquals(false, definition.isEvent("DOHH"));
    }

    @Test
    public void testEventsAreProperlyDefined() throws StateMachineDefinitionException {
        assertEquals(true, definition.isEvent(EVENT_AB));
        assertEquals(5, definition.getEvents().size());
    }

    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testStateCannotBeStartAndFinal() throws StateMachineDefinitionException {
        StateMachineDefinitionImpl def = new StateMachineDefinitionImpl();
        def.defineState("TEST", true, true);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDefineNullEvent() throws StateMachineException {
        definition.defineEvent(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDefineNullState() throws StateMachineException {
        definition.defineState(null);
    }

    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testDefineTransitionWithNullSourceState() throws StateMachineDefinitionException {
        definition.defineTransition(null, EVENT_AB, STATE_B, transitionController);
    }

    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testDefineTransitionWithNullTargetState() throws StateMachineDefinitionException {
        definition.toString();
        definition.defineTransition(STATE_A, EVENT_AB, null, transitionController);
    }

    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testDefineTransitionWithNullEvent() throws StateMachineDefinitionException {
        definition.defineTransition(STATE_A, null, STATE_B, transitionController);
    }

    @Test(expectedExceptions = ConstraintException.class)
    public void testAddingTransationToAFinalState() throws StateMachineDefinitionException {
        definition.defineTransition(STATE_C, EVENT_CC, STATE_B, transitionController);
    }

    public void testAddingReflexiveTransitionToAFinalState() throws StateMachineDefinitionException {
        definition.defineTransition(STATE_C, STATE_C, EVENT_CC, transitionController);

        assertTrue(definition.getApplicableEvents(STATE_C).contains(EVENT_CC));
    }

    @Test(expectedExceptions = TransitionNotDefinedException.class)
    public void testNonDefinedTransitions() throws StateMachineException {
        StateMachineDefinition definition = createMachineDefinition();
        StateMachineImpl sm = new NonReentrantStateMachine(definition);

        sm.processEvent(EVENT_AB, null);
        sm.processEvent(EVENT_BB, null);
        sm.processEvent(EVENT_BC, null);
        sm.processEvent(EVENT_AB, null);
    }

    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testMoreThanOneStartState() throws StateMachineException {
        definition.defineState(STATE_A, true, false);
    }

    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testFinalStateAsStart() throws StateMachineDefinitionException {
        definition.defineState("STATE_D", true, true);
    }
}
