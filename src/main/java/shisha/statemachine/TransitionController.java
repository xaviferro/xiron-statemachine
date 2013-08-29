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

/**
 * Each transition is executed in three phases. The phase most widely used is the
 * {@link #phaseTransition} one. So, if you don't have any special need, use that one.
 * 
 * These phases are executed sequentially:
 * <ul>
 * <li>First, the {@link #exitStatePhase} is invoked. It's the only phase that allows
 * 	   cancelling the transition. If so, none of the following phases are going to
 *     be executed.</li>
 *     
 * <li>Second, the {@link #transitionPhase} is invoked. It should contain the main work to
 *     be performed during a transition</li>
 *     
 * <li>And finally, the {@link #enterStatePhase} is invoked. It's the phase that allows
 * 	   us to return a forward. The method returns a {@link EventInfo} object that might.
 *     If not null, the event that is inside the object is going to be executed
 *     by the state machine without releasing the lock. This is very useful in certain
 *     circumstances (specially ghost-like condition states that we need to check a lot of
 *     conditions for taking a decision about the next actions to happen)</li>
 * </ul>
 */
public interface TransitionController {
    void execute(TransitionInfo event);
}
