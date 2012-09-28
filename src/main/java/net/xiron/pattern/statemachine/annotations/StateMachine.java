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
package net.xiron.pattern.statemachine.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an annotated state machine. The state machine
 * might contain:
 * <ul>
 *  <li>Annotated states {@link State}</li>
 *  <li>Annotated events {@link Event}</li>
 *  <li>Annotated transitions {@link Transition}</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StateMachine {
    /**
     * When defining a state machine, one can define the reentrant/thread
     * strategy. Read {@link Strategies} in detail
     */
    Strategies strategy() default Strategies.NON_REENTRANT;
}
