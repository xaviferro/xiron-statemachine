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
package net.xiron.pattern.statemachine.annotated;

import net.xiron.pattern.statemachine.PhaseEnterResult;
import net.xiron.pattern.statemachine.TransitionInfo;
import net.xiron.pattern.statemachine.annotations.Event;
import net.xiron.pattern.statemachine.annotations.State;
import net.xiron.pattern.statemachine.annotations.StateMachine;
import net.xiron.pattern.statemachine.annotations.Strategies;
import net.xiron.pattern.statemachine.annotations.Transition;
import net.xiron.pattern.statemachine.annotations.TransitionPhases;

@StateMachine(strategy=Strategies.NON_REENTRANT)
public class DumbRedirectController {
    @State(isStart=true) public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    
    @Event public static final String EVENT_AA = "EVENT_AA";
    @Event public static final String EVENT_AB = "EVENT_AB";
    
    @Transition(source=STATE_A,event=EVENT_AA,target=STATE_A,phase=TransitionPhases.PHASE_ENTER)
    public PhaseEnterResult transitionAB(TransitionInfo evt) {
        return new PhaseEnterResult(EVENT_AB, null);
    }
    
    @Transition(source=STATE_A,event=EVENT_AB,target=STATE_B,phase=TransitionPhases.PHASE_TRANSITION)
    public void noop(TransitionInfo evt) {}
}
