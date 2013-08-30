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
package shisha.pattern.statemachine.annotated;

import org.testng.annotations.Test;

import shisha.pattern.statemachine.annotated.util.IllegalEventAnnotation_01;
import shisha.pattern.statemachine.annotated.util.IllegalEventAnnotation_02;
import shisha.pattern.statemachine.annotated.util.IllegalStateAnnotation_01;
import shisha.pattern.statemachine.annotated.util.IllegalStateAnnotation_02;
import shisha.pattern.statemachine.annotated.util.IllegalTransitionAnnotation_01;
import shisha.statemachine.StateMachines;
import shisha.statemachine.exceptions.IllegalAnnotationException;
import shisha.statemachine.exceptions.IllegalEventAnnotationException;
import shisha.statemachine.exceptions.IllegalStateAnnotationException;
import shisha.statemachine.exceptions.IllegalTransitionAnnotationException;
import shisha.statemachine.exceptions.StateMachineDefinitionException;
import shisha.statemachine.exceptions.StateMachineException;

public class IllegalAnnotatedControllerTest {
    @Test(expectedExceptions = IllegalEventAnnotationException.class)
    public void testIllegalEvent_01() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalEventAnnotation_01());
    }

    @Test(expectedExceptions = IllegalStateAnnotationException.class)
    public void testIllegalState_01() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalStateAnnotation_01());
    }

    @Test(expectedExceptions = IllegalStateAnnotationException.class)
    public void testIllegalState_02() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalStateAnnotation_02());
    }

    @Test(expectedExceptions = IllegalTransitionAnnotationException.class)
    public void testIllegalTransition_01() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalTransitionAnnotation_01());
    }

    /**
     * Checks that we don't read private static final String events
     */
    @Test
    public void testIllegalEvent_02() throws StateMachineException {
        StateMachines.newNonReentrant(new IllegalEventAnnotation_02());
    }
}
