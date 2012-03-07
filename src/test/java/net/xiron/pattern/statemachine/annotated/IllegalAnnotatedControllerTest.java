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

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.IllegalAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalEventAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalStateAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalTransitionAnnotationException;
import net.xiron.pattern.statemachine.exceptions.StateMachineException;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;

import org.junit.Test;

public class IllegalAnnotatedControllerTest  {
    @Test(expected=IllegalEventAnnotationException.class)
    public void testIllegalEvent_01() throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException {
        new AnnotatedControllerProcessor(new IllegalEventAnnotation_01());
    }
    
    @Test(expected=IllegalStateAnnotationException.class)
    public void testIllegalState_01() throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException {
        new AnnotatedControllerProcessor(new IllegalStateAnnotation_01());
    } 
    
    @Test(expected=IllegalStateAnnotationException.class)
    public void testIllegalState_02() throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException {
        new AnnotatedControllerProcessor(new IllegalStateAnnotation_02());
    } 
    
    @Test(expected=IllegalTransitionAnnotationException.class)
    public void testIllegalTransition_01() throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException {
        new AnnotatedControllerProcessor(new IllegalTransitionAnnotation_01());
    } 
    
    /**
     * Checks that we don't read private static final String events
     */
    @Test public void testIllegalEvent_02() throws StateMachineException {
        new AnnotatedControllerProcessor(new IllegalEventAnnotation_02());
    }
}
