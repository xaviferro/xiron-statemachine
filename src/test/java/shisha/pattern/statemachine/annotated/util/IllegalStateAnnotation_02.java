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
package shisha.pattern.statemachine.annotated.util;

import shisha.statemachine.annotations.AStateMachine;
import shisha.statemachine.annotations.State;

/**
 * State should be a public static String
 */
@AStateMachine
public class IllegalStateAnnotation_02 {
    @State public static final int STATE_A = 1;
}
