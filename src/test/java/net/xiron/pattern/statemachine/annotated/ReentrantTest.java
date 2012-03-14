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

import junit.framework.Assert;
import net.xiron.pattern.statemachine.TransitionEvent;
import net.xiron.pattern.statemachine.exceptions.StateMachineException;

import org.junit.Test;

public class ReentrantTest implements AnnotatedController {
    @StartState @State public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    @State public static final String STATE_C = "STATE_C";
    
    @Event public static final String EVENT_AA = "EVENT_AA";
    @Event public static final String EVENT_AB = "EVENT_AB";
    
    private AnnotatedControllerProcessor processor;
    private int counter = 0;
    
    @Transition(source=STATE_A,target=STATE_A,event=EVENT_AA)
    public void transition_AB(TransitionEvent evt) throws StateMachineException {
        if (counter < 10) {
            counter++;
            processor.processEvent(EVENT_AA, null);
            processor.processEvent(EVENT_AA, null);
        } else if (counter == 10) {
            counter++;
            processor.processEvent(EVENT_AB, null);
        }
    }
    
    @Transition(source=STATE_A,target=STATE_B,event=EVENT_AB)
    public void noop(TransitionEvent evt) {}
    
    @Test
    public void test() throws StateMachineException {
        processor = new AnnotatedControllerProcessor(this, true);
        processor.processEvent(EVENT_AA, null);
        Assert.assertEquals(processor.getStateMachine().getCurrentState(), STATE_B);
    }
}
