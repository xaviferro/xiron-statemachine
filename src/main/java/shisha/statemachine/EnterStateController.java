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
package shisha.statemachine;

/**
 * Corresponds to the enter state phase. During this phase, the state machine allows
 * us to force the state machine to process another event without releasing the lock, 
 * so we don't need to fight with other threads. 
 * 
 * <p>
 * The method returns a {@link EventInfo} object. If not null, the event that is inside the object 
 * is going to be executedby the state machine without releasing the lock. This is very useful in certain
 * circumstances (specially ghost-like condition states that we need to check a lot of
 * conditions for taking a decision about the next actions to happen)</li>
 */
public interface EnterStateController {
    public EventInfo execute(TransitionInfo event);
}
