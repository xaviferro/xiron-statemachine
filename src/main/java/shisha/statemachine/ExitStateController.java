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
 * The only transition phase that we could cancel the transition if we return
 * false
 */
public interface ExitStateController {
    /**
     * If we return <code>false</code> we will cancel the transition
     * 
     * @return returns a boolean for continuing the transition or not.
     */
    Boolean execute(TransitionInfo event);
}
