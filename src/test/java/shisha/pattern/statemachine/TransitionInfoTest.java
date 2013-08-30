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
package shisha.pattern.statemachine;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import shisha.statemachine.EventInfo;
import shisha.statemachine.StateMachine;
import shisha.statemachine.StateMachines;
import shisha.statemachine.TransitionInfo;
import shisha.statemachine.annotations.AStateMachine;
import shisha.statemachine.annotations.EnterState;
import shisha.statemachine.annotations.Event;
import shisha.statemachine.annotations.ExitState;
import shisha.statemachine.annotations.State;
import shisha.statemachine.annotations.Transition;

@AStateMachine
public class TransitionInfoTest {
    @State(isStart=true) public final static String STATE_A = "STATE_A";
    @State public final static String STATE_B = "STATE_B";
    @Event public final static String EVENT_AB = "EVENT_AB";
    
    private void assertTransitionInfo(TransitionInfo info) {
        assertEquals(info.getSource(), STATE_A);
        assertEquals(info.getTarget(), STATE_B);
        assertEquals(info.getEvent(), EVENT_AB);
        assertEquals(info.getObject(), EVENT_AB);
    }
    
    @ExitState(STATE_A)
    public Boolean onExitA(TransitionInfo info) {
        info.getTransitionContext().put("HELLO", "WORLD");
        assertTransitionInfo(info);
        return true;
    }
    
    @Transition(source = STATE_A, event = EVENT_AB , target = STATE_B)
    public void onTransition(TransitionInfo info) {
        assertNotNull(info.getTransitionContext().get("HELLO"));
        assertEquals(info.getTransitionContext().get("HELLO"), "WORLD");
        info.getTransitionContext().put("GOODBYE","HELL");
        
        assertTransitionInfo(info);
    }
    
    @EnterState(STATE_B)
    public EventInfo onEnter(TransitionInfo info) {
        assertNotNull(info.getTransitionContext().get("HELLO"));
        assertEquals(info.getTransitionContext().get("HELLO"), "WORLD");
        assertNotNull(info.getTransitionContext().get("GOODBYE"));
        assertEquals(info.getTransitionContext().get("GOODBYE"), "HELL");
        
        assertTransitionInfo(info);
        return null;
    }
    
    
    @Test
	public void testAddingObjectTransitionContext() {
        String source = "SOURCE";
        String target = "TARGET";
        String event = "EVENT";
        TransitionInfo ti = new TransitionInfo(source, event, target, null);
        ti.getTransitionContext().put("key", "value");
        
        assertTrue(ti.getTransitionContext().containsKey("key"));
    }
    
    @Test
    public void testTransitionInfoIsPassedProperlyToAllPhases() throws Exception {
        StateMachine stateMachine = StateMachines.newNonReentrant(this);
        stateMachine.processEvent(EVENT_AB, EVENT_AB);
        
        assertEquals(stateMachine.getCurrentState(), STATE_B);
    }
}
