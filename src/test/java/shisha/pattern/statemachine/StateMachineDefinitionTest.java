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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import shisha.pattern.statemachine.util.DumbController;
import shisha.statemachine.StateMachineDefinition;
import shisha.statemachine.StateMachineDefinitionImpl;
import shisha.statemachine.StateMachineImpl;
import shisha.statemachine.exceptions.ConstraintException;
import shisha.statemachine.exceptions.EventAlreadyExistsException;
import shisha.statemachine.exceptions.StateAlreadyExistsException;
import shisha.statemachine.exceptions.StateMachineDefinitionException;
import shisha.statemachine.exceptions.StateMachineException;
import shisha.statemachine.exceptions.StateNotDefinedException;
import shisha.statemachine.exceptions.TransitionNotDefinedException;
import shisha.statemachine.strategy.NonReentrantStrategy;

public class StateMachineDefinitionTest {
    public static String STATE_A = "STATE_A";
    public static String STATE_B = "STATE_B";
    public static String STATE_C = "STATE_C";

    public static String EVENT_AB = "EVENT_AB";
    public static String EVENT_BC = "EVENT_BC";
    public static String EVENT_BB = "EVENT_BB";
    public static String EVENT_BA = "EVENT_BA";
    public static String EVENT_CC = "EVENT_CC";

    private StateMachineDefinition definition;
    
    private StateMachineDefinition createMachineDefinition()
            throws StateMachineDefinitionException 
    {
        StateMachineDefinition definition = new StateMachineDefinitionImpl();
        definition.defineEvent(EVENT_AB);
        definition.defineEvent(EVENT_BC);
        definition.defineEvent(EVENT_BB);
        definition.defineEvent(EVENT_BA);
        definition.defineEvent(EVENT_CC);

        definition.defineState(STATE_A, true, false);
        definition.defineState(STATE_B);
        definition.defineState(STATE_C, false, true);

        definition.defineTransition(STATE_A, STATE_B, EVENT_AB);
        definition.defineTransition(STATE_B, STATE_C, EVENT_BC);
        definition.defineTransition(STATE_B, STATE_B, EVENT_BB);
        definition.defineTransition(STATE_B, STATE_A, EVENT_BA);
        
        return definition;
    }
    
    @BeforeMethod
    public void beforeAnyMethod() throws StateMachineDefinitionException {
        definition = createMachineDefinition();        
    }
    
    @Test(expectedExceptions = ConstraintException.class)
    public void testDefineStateAsStartAndFinal() throws StateMachineDefinitionException {
        definition.defineState("DOHH", true, true);
    }
    
    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testAddTransitionToFinalState() throws StateMachineDefinitionException {
        definition.defineTransition(STATE_C, STATE_A, EVENT_AB);
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
    
    @Test (expectedExceptions = StateNotDefinedException.class)
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
        StateMachineDefinition def = new StateMachineDefinitionImpl();
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
        definition.defineTransition(null, STATE_B, EVENT_AB);
    }
    
    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testDefineTransitionWithNullTargetState() throws StateMachineDefinitionException {
        definition.toString();
        definition.defineTransition(STATE_A, null, EVENT_AB);
    }
    
    @Test(expectedExceptions = StateMachineDefinitionException.class)
    public void testDefineTransitionWithNullEvent() throws StateMachineDefinitionException {
        definition.defineTransition(STATE_A, STATE_B, null);
    }
    
    @Test(expectedExceptions = ConstraintException.class)
    public void testAddingTransationToAFinalState() throws StateMachineDefinitionException {
    	definition.defineTransition(STATE_C, STATE_B, EVENT_CC);
    }
    
    public void testAddingReflexiveTransitionToAFinalState() throws StateMachineDefinitionException {
    	definition.defineTransition(STATE_C, STATE_C, EVENT_CC);
    	
    	assertTrue(definition.getApplicableEvents(STATE_C).contains(EVENT_CC));
    }

    @Test(expectedExceptions = TransitionNotDefinedException.class)
    public void testNonDefinedTransitions() throws StateMachineException {
        StateMachineDefinition definition = createMachineDefinition();
        NonReentrantStrategy strategy = new NonReentrantStrategy();
        StateMachineImpl sm = new StateMachineImpl(definition, strategy);

        DumbController dc = new DumbController(true);
        sm.processEvent(EVENT_AB, null, dc);
        sm.processEvent(EVENT_BB, null, dc);
        sm.processEvent(EVENT_BC, null, dc);
        sm.processEvent(EVENT_AB, null, dc);
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
