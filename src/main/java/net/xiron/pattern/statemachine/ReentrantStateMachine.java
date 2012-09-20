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

/**
 * Reentrant state machine allows consuming events while performing transitions.
 * They are not consumed immediately, they are enqueued and consumed when
 * the state machine is available.
 * 
 * <p>
 * In order to achieve this, EACH ReentrantStateMachine will run in its own 
 * thread. So, the decision of using reentrant state machines should be taken
 * very carefully as it might end up as an important source of problems.
 */
public interface ReentrantStateMachine extends StateMachine {
    void close();
    
    void processEvent(String event, Object object)
        throws EventNotDefinedException;
    
    void processEvent(String event, 
                      Object object, 
                      TransitionController execution)
        throws EventNotDefinedException;
    
    void processEvent(String event, 
                      Object object, 
                      TransitionController execution, 
                      TransitionLifecycleController lifecycle)
        throws EventNotDefinedException;
}
