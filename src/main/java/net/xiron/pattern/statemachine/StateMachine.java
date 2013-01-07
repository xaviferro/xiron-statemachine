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

import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateMachineDefinitionException;

/**
 * Major entry point for processing events in a state machine. The state machine
 * is defined by {@link StateMachineDefinition} and the strategy of the machine
 * is defined by {@link StateMachineStrategy}.
 */
public interface StateMachine {
    /**
     * Modifies the current state. A developer should NEVER modify its value
     * unless he/she is very sure what he is doing
     */
    public void setCurrentState(String state);

    /**
     * Returns the current state of the state machine
     */
    public String getCurrentState();

    /**
     * Returns the state machine definition 
     */
    public StateMachineDefinition getDefinition();

    /**
     * Returns the strategy of the state machine
     */
    public StateMachineStrategy getStrategy();
    
    /**
     * Consumes an event following the strategy defined by {@link #getStrategy()}
     */
    public void processEvent(String event, Object object,
                             TransitionController controller)
            throws ReentrantTransitionNotAllowed, 
                   StateMachineDefinitionException;
}
