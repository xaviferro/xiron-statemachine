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
package net.xiron.pattern.statemachine.strategy;

import net.xiron.pattern.statemachine.StateMachine;
import net.xiron.pattern.statemachine.annotations.AnnotatedControllerProcessor;
import net.xiron.pattern.statemachine.exceptions.StateMachineException;

import org.junit.Test;

/**
 * Testing the strategy itself
 */
public class ReentrantEnqueueStrategyTest {
    @Test
    public void reentrantStrategy() throws StateMachineException {
        AnnotatedControllerProcessor processor = new AnnotatedControllerProcessor(new ReentrantEnqueueStateMachine());
        StateMachine sm = processor.getStateMachine();
        //sm.processEvent(event, object, controller, observer)
    }
}
