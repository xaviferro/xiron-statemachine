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
package shisha.statemachine;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shisha.statemachine.exceptions.ReentrantTransitionNotAllowed;
import shisha.statemachine.exceptions.StateMachineDefinitionException;

/**
 * Basic state machine implementation. Implements a non-reentrant transition
 * strategy
 */
public abstract class StateMachineImpl implements StateMachine {
    protected Logger l = LoggerFactory.getLogger(getClass());

    protected String currentState;
    protected StateMachineDefinition definition;
    protected StateMachineStrategy strategy;

    public StateMachineImpl(StateMachineDefinition definition,
            StateMachineStrategy strategy) {
        this.definition = definition;
        this.strategy = strategy;
        this.currentState = definition.getStartState();
    }

    /**
     * The state machine object is the entry point to the state management world.
     * The state machine is defined by the {@link StateMachineDefinition} and the
     * execution strategy is defined by {@link StateMachineStrategy}
     * 
     * <p>This method delegates completely on the strategy.
     */
    public void processEvent(String event, Object object)
            throws ReentrantTransitionNotAllowed,
                   StateMachineDefinitionException 
    {
        strategy.processEvent(this, event, object);
    }

    public StateMachineDefinition getDefinition() {
        return this.definition;
    }

    public StateMachineStrategy getStrategy() {
        return this.strategy;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        l.debug("#setCurrentState: " + currentState);
        this.currentState = currentState;
    }

    /**
     * Returns the state machine definition in a XML format. This is not a cheap
     * operation.
     */
    public String toString() {
        return this.definition.toString();
    }
}
