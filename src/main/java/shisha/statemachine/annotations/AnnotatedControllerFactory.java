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
package shisha.statemachine.annotations;

import shisha.statemachine.StateMachineStrategy;
import shisha.statemachine.exceptions.IllegalAnnotationException;
import shisha.statemachine.exceptions.StateMachineDefinitionException;
import shisha.statemachine.strategy.NonReentrantStrategy;
import shisha.statemachine.strategy.ReentrantStrategy;

/**
 * Allows creating a state machine, selecting any available strategy,
 * passing an object that is annotated
 */
public class AnnotatedControllerFactory {
    public AnnotatedControllerProcessor createNonReentrantStateMachine(Object object) throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachineStrategy strategy = new NonReentrantStrategy();
        return new AnnotatedControllerProcessor(strategy, object);
    }
    
    public AnnotatedControllerProcessor createReentrantProcessor(Object object) throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachineStrategy strategy = new ReentrantStrategy();
        return new AnnotatedControllerProcessor(strategy, object);
    }
    
//    public AnnotatedControllerProcessor createEnqueueProcessor(Object object) throws StateMachineDefinitionException, IllegalAnnotationException {
//        StateMachineStrategy strategy = new ReentrantEnqueueStrategy();
//        return new AnnotatedControllerProcessor(strategy, object);
//    }
}
