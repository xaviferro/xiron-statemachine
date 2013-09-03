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

import shisha.statemachine.exceptions.StateMachineDefinitionException;
import shisha.statemachine.exceptions.StateMachineExecutionException;

/**
 * We keep the definition of the state machine in the {@link StateMachineDefinition} 
 * class, but this class contains the current state based on the definition.
 * It would be like the processor in a CPU: given a {@link StateMachineDefinition}, it keeps
 * the state of execution.
 * 
 * Some concepts to clarify:
 * <ul>
 * <li>Once the state machine is instantiated directly or via the 
 *     {@link shisha.statemachine.StateMachines} helper, the current state
 *     is set to the initial state</li>
 *     
 * <li>State machines are designed to protect critical sections in a complex
 *     event driven environment. Only one thread CAN execute a transition at a time.
 *     Any manipulation of sensitive information SHOULD be done during a transition.</li>
 * 
 * <li>So, during a transition the lock of the object is acquired and it won't
 *     be released until the transition finishes. Be aware of that because it might
 *     cause deadlocks if you are not a good programmer :-)</li>
 *     
 * <li>Using the lock guarantees no other thread will be in the critical
 *     section. But, what about the same thread? It might be possible to process an
 *     event while processing another event. We want to avoid that because it might
 *     cause inconsistencies. So, the {@link shisha.statemachine.strategy.ReentrantStrategy}
 *     allows the same thread to consume events during a transition whereas the 
 *     {@link shisha.statemachine.strategy.NonReentrantStrategy} is more restrictive
 *     and doesn't allow that</li>
 * 
 * <li>Based on my experience, non reentrant strategies are safer and force better
 *     approaches in a highly concurrent system. So, use the reentrant under your
 *     responsibility</li>
 * 
 * <li>Do not forget that you can always force the state machine to process an event
 *     during a transition without releasing the lock: use the {@link EnterStateController}
 *     for that</li>
 * </ul>
 */
public interface StateMachine {
    /**
     * Returns the current state of the state machine
     */
    public String getCurrentState();

    /**
     * Returns the state machine definition
     */
    public StateMachineDefinition getDefinition();

    /**
     * Consumes an event following the selected strategy.
     */
    public void processEvent(String event, Object object) throws StateMachineExecutionException,
            StateMachineDefinitionException;
}
