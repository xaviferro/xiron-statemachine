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
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

public interface StateMachine {
    /**
     * Modifies the current state. A developer should NEVER modify its value
     * unless he/she is very sure what he is doing
     */
    public void setCurrentState(String state);

    public String getCurrentState();

    public StateMachineDefinition getStateMachineDefinition();

    public void processEvent(String event, Object object,
                             TransitionController controller,
                             TransitionLifecycleController observer)
            throws ReentrantTransitionNotAllowed, 
                   EventNotDefinedException,
                   TransitionNotDefinedException;
}
