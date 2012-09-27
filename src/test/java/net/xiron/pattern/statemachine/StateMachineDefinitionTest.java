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

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.StateMachineException;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;
import net.xiron.pattern.statemachine.strategy.NonReentrantStrategy;

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
        throws StateNotDefinedException, EventNotDefinedException 
    {
        if (definition == null) {
            definition = new StateMachineDefinitionImpl();
            definition.defineEvent(EVENT_AB);
            definition.defineEvent(EVENT_BC);
            definition.defineEvent(EVENT_BB);
            definition.defineEvent(EVENT_BA);
            
            definition.defineState(STATE_A);
            definition.defineState(STATE_B);
            definition.defineState(STATE_C);
            
            definition.defineTransition(STATE_A, STATE_B, EVENT_AB);
            definition.defineTransition(STATE_B, STATE_C, EVENT_BC);
            definition.defineTransition(STATE_B, STATE_B, EVENT_BB);
            definition.defineTransition(STATE_B, STATE_A, EVENT_BA);
            
            definition.setStartState(STATE_A);
            
            System.err.println(definition);
        }
        
        return definition;
    }
    
    @Test(expected=TransitionNotDefinedException.class)
    public void testSuccessfulTransitions() throws StateMachineException {
        StateMachineDefinition definition = createMachineDefinition();
        NonReentrantStrategy strategy = new NonReentrantStrategy();
        StateMachineImpl sm = new StateMachineImpl(definition, strategy);
        
        DumbController dc = new DumbController(true);
        sm.processEvent(EVENT_AB, null, dc, null);
        sm.processEvent(EVENT_BB, null, dc, null);
        sm.processEvent(EVENT_BC, null, dc, null);
        sm.processEvent(EVENT_AB, null, dc, null);
    }
}
