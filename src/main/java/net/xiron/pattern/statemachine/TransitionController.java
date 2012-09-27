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

/**
 * Each transition is splitted into three phases. The phase most used should be the
 * @link{#phaseTransition} one.
 * 
 * They are executed sequentially:
 * <ul>
 * <li>First, the method @link{#phaseExitState} is executed. Allows cancelling the transition.</li>
 * <li>Second, the method @link{#phaseTransition} is executed. </li>
 * <li>And third, the method @link{#phaseEnterState} is executed. It allows forcing another
 *     event to be processed without releasing the lock. This might be useful for some
 *     circumstances (specially ghost-like condition states that we need to check a lot of
 *     conditions for performing other actions)</li>
 * </ul>
 */
public interface TransitionController {
    /**
     * First phase of the transition.The one and only phase that allows stopping the transition. 
     * Returning false will stop the event propagation.
     * 
     * @return true in case we want to continue with the transition. 
     *         false will cancel the transition and will keep the current state as
     *               it was before the transition.
     */
    boolean exitStatePhase(TransitionEvent event);
    
    /**
     * The transition phase itself.
     */
    void transitionPhase(TransitionEvent event);
    
    /**
     * Third step, executed when entering a state.
     * 
     * @return the event we want to execute next right after this transition -as there
     *         might be other threads trying to perform other transitions-. 
     *         @Null means we don't want to do anything special.
     */
    PhaseEnterResult enterStatePhase(TransitionEvent event);
}
