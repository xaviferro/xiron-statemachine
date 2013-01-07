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
package net.xiron.pattern.statemachine;

import static org.junit.Assert.assertEquals;
import net.xiron.pattern.statemachine.exceptions.ConstraintException;
import net.xiron.pattern.statemachine.exceptions.EventAlreadyExistsException;
import net.xiron.pattern.statemachine.exceptions.StateAlreadyExistsException;
import net.xiron.pattern.statemachine.exceptions.StateMachineDefinitionException;
import net.xiron.pattern.statemachine.exceptions.StateMachineException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;
import net.xiron.pattern.statemachine.strategy.NonReentrantStrategy;

import org.junit.Before;
import org.junit.Test;

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

        definition.defineState(STATE_A, true, false);
        definition.defineState(STATE_B);
        definition.defineState(STATE_C, false, true);

        definition.defineTransition(STATE_A, STATE_B, EVENT_AB);
        definition.defineTransition(STATE_B, STATE_C, EVENT_BC);
        definition.defineTransition(STATE_B, STATE_B, EVENT_BB);
        definition.defineTransition(STATE_B, STATE_A, EVENT_BA);
        
        return definition;
    }
    
    @Before
    public void beforeAnyMethod() throws StateMachineDefinitionException {
        definition = createMachineDefinition();        
    }
    
    @Test(expected = ConstraintException.class)
    public void testDefineStateAsStartAndFinal() throws StateMachineDefinitionException {
        definition.defineState("DOHH", true, true);
    }
    
    @Test(expected = StateMachineDefinitionException.class)
    public void testAddTransitionToFinalState() throws StateMachineDefinitionException {
        definition.defineTransition(STATE_C, STATE_A, EVENT_AB);
    }
    
    @Test(expected = StateAlreadyExistsException.class) 
    public void testStateAlreadyExists() throws StateMachineDefinitionException {
        definition.defineState(STATE_A);
    }
    
    @Test(expected = EventAlreadyExistsException.class)
    public void testEventAlreadyExists() throws StateMachineDefinitionException {
        definition.defineEvent(EVENT_AB);
    }
    
    @Test
    public void testStartState() throws StateMachineDefinitionException {
        assertEquals(STATE_A, definition.getStartState());
    }
    
    @Test
    public void testDefinedTransition() throws StateMachineDefinitionException {
        assertEquals(true, definition.isState(STATE_A));
        assertEquals(STATE_B, definition.getTargetState(STATE_A, EVENT_AB));
        assertEquals(1, definition.getEvents(STATE_A).size());
    }
    
    @Test
    public void testDefinedStates() throws StateMachineDefinitionException {
        assertEquals(true, definition.isState(STATE_A));
        assertEquals(true, definition.isState(STATE_B));
        assertEquals(true, definition.isState(STATE_C));
        assertEquals(3, definition.getStates().size());
    }
    
    @Test
    public void testNotDefinedState() throws StateMachineDefinitionException {
        assertEquals(false, definition.isState("DOHH"));
    }
    
    @Test
    public void testNotDefinedEvent() throws StateMachineDefinitionException {
        assertEquals(false, definition.isEvent("DOHH"));
    }
    
    @Test
    public void testDefinedEvents() throws StateMachineDefinitionException {
        assertEquals(true, definition.isEvent(EVENT_AB));
        assertEquals(4, definition.getEvents().size());
    }
    
    @Test(expected = StateMachineDefinitionException.class)
    public void testStateIsStartAndFinal() throws StateMachineDefinitionException {
        StateMachineDefinition def = new StateMachineDefinitionImpl();
        def.defineState("TEST", true, true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDefineNullEvent() throws StateMachineException {
        definition.defineEvent(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDefineNullState() throws StateMachineException {
        definition.defineState(null);
    }
    
    @Test(expected = StateMachineDefinitionException.class)
    public void testDefineTransitionWithNullSourceState() throws StateMachineDefinitionException {
        definition.defineTransition(null, STATE_B, EVENT_AB);
    }
    
    @Test(expected = StateMachineDefinitionException.class)
    public void testDefineTransitionWithNullTargetState() throws StateMachineDefinitionException {
        definition.toString();
        definition.defineTransition(STATE_A, null, EVENT_AB);
    }
    
    @Test(expected = StateMachineDefinitionException.class)
    public void testDefineTransitionWithNullEvent() throws StateMachineDefinitionException {
        definition.defineTransition(STATE_A, STATE_B, null);
    }

    @Test(expected = TransitionNotDefinedException.class)
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

    @Test(expected = StateMachineDefinitionException.class)
    public void testMoreThanOneStartState() throws StateMachineException {
        definition.defineState(STATE_A, true, false);
    }

    @Test(expected = StateMachineDefinitionException.class)
    public void testFinalStateAsStart() throws StateMachineDefinitionException {
        definition.defineState("STATE_D", true, true);
    }
}
