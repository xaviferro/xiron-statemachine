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
package shisha.pattern.statemachine.strategy;


import org.testng.Assert;
import org.testng.annotations.Test;

import shisha.statemachine.EventInfo;
import shisha.statemachine.TransitionInfo;
import shisha.statemachine.annotations.AnnotatedControllerFactory;
import shisha.statemachine.annotations.AnnotatedControllerProcessor;
import shisha.statemachine.annotations.Event;
import shisha.statemachine.annotations.State;
import shisha.statemachine.annotations.Transition;
import shisha.statemachine.annotations.TransitionPhases;
import shisha.statemachine.exceptions.EventNotDefinedException;
import shisha.statemachine.exceptions.StateMachineException;

public class NonReentrantStrategyTest {
    @State(isStart = true)
    public static final String STATE_A = "STATE_A";
    @State
    public static final String STATE_B = "STATE_B";
    @State
    public static final String STATE_C = "STATE_C";

    @Event
    public static final String EVENT_AB = "EVENT_AB";
    @Event
    public static final String EVENT_BC = "EVENT_BC";

    private AnnotatedControllerProcessor processor;

    @Transition(source = STATE_A, target = STATE_B, event = EVENT_AB, phase = TransitionPhases.PHASE_ENTER)
    public EventInfo transitionAB(TransitionInfo evnt)
            throws StateMachineException {
        processor.processEvent(EVENT_BC, null);
        return null;
    }

    @Test
    public void testDefinedTransition() throws StateMachineException {
        AnnotatedControllerFactory f = new AnnotatedControllerFactory();
        this.processor = f
                .createNonReentrantStateMachine(this);
        this.processor.processEvent(EVENT_AB, null);
        Assert.assertEquals(processor.getStateMachine().getCurrentState(),
                STATE_B);
    }
    
    @Test( expectedExceptions = EventNotDefinedException.class )
    public void testProcessingNotDefinedEvent() throws StateMachineException {
    	AnnotatedControllerFactory f = new AnnotatedControllerFactory();
        processor = f
                .createNonReentrantStateMachine(this);
        processor.processEvent("NON_EXISTENT", null);
    }
}
