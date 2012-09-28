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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic state machine implementation. Implements a non-reentrant transition
 * strategy
 */
public class StateMachineImpl implements StateMachine {
    private static Logger l = LoggerFactory.getLogger(StateMachineImpl.class);
    
    private String currentState;
    private StateMachineDefinition definition;
    private StateMachineStrategy strategy;
    
    public StateMachineImpl(StateMachineDefinition definition, StateMachineStrategy strategy) {
        this.definition = definition;
        this.strategy = strategy;
        this.currentState = definition.getStartState();
    }
    
    /**
     * TODO. We could extract this functionality and apply a Strategy pattern. It would allow
     * us to have different implementations (NonReentrant, Reentrant -with a queue of things-),
     * or just set up a flag when starting. For the time being, we keep the NonReentrant one.
     * 
     * @see net.xiron.pattern.statemachine.StateMachineDefinition#processEvent(java.lang.String, java.lang.Object)
     */
    @Override 
    public synchronized void processEvent(String event, 
                                          Object object,
                                          TransitionController controller,
                                          TransitionObserver observer)
        throws ReentrantTransitionNotAllowed, StateMachineDefinitionException 
    {
        strategy.processEvent(this, event, object, controller, observer);
    }
    
    @Override
    public StateMachineDefinition getStateMachineDefinition() {
        return this.definition;
    }
    
    @Override
    public String getCurrentState() {
        return currentState;
    }
    
    @Override
    public void setCurrentState(String currentState) {
        l.debug("#setCurrentState: " + currentState);
        this.currentState = currentState;
    }
    
    /**
     * Returns the state machine definition in a XML format. This is not
     * a cheap operation.
     */
    @Override 
    public String toString() {
        return this.definition.toString();
    }
}
