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
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateMachineDefinitionException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

public interface StateMachineStrategy {
    /**
     * Checks that current event is allowed for the current state. That means that a
     * transition has been defined for this state machine.
     * 
     * If the transition has been defined, this method will perform:
     * 1 - execute the transitionExitPhase
     * 2 - execute the transition
     * 3 - execute the transitionEnterPhase
     * 
     * Everything will happen with the state machine lock acquired, so careful with the
     * deadlocks.
     * 
     * @param event the event that we want to process.
     *  
     * @param object if we need an object to be passed to the controller with
     *        context meaning.
     * 
     * @param controller controls all the phases of executing a transaction
     * 
     * @throws EventNotDefinedException
     * @throws ReentrantTransitionNotAllowed
     * @throws TransitionNotDefinedException
     */
    public void processEvent(StateMachine statemachine,
                             String event,
                             Object object,
                             TransitionController controller,
                             TransitionObserver lifecycle)
        throws ReentrantTransitionNotAllowed, StateMachineDefinitionException;
}
