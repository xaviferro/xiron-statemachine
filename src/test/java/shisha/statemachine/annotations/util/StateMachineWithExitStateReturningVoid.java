/*  
 * Copyright 2012-2013 xavi.ferro
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
package shisha.statemachine.annotations.util;

import shisha.statemachine.TransitionInfo;
import shisha.statemachine.annotations.AStateMachine;
import shisha.statemachine.annotations.Event;
import shisha.statemachine.annotations.ExitState;
import shisha.statemachine.annotations.State;
import shisha.statemachine.annotations.Transition;

@AStateMachine
public class StateMachineWithExitStateReturningVoid {
    @State(isStart=true) public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    @Event public static final String EVENT_AB = "AB";
    
    @Transition(source = STATE_A, target = STATE_B, event = EVENT_AB)
    public void noop(TransitionInfo info) { }
    
    @ExitState(STATE_A)
    public void enterState(TransitionInfo info) {
        System.out.println("Exiting A");
    }
}
