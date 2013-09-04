package shisha.statemachine.annotations.util;

import junit.framework.Assert;

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
import shisha.statemachine.annotations.Transitions;
import shisha.statemachine.exceptions.StateMachineException;

@AStateMachine
public class StateMachineWithRedirectOnEnterPhase {
    @State(isStart=true) public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    @State public static final String STATE_COND = "STATE_COND";
    @State public static final String STATE_D = "STATE_D";
    
    @Event public static final String EVENT_AB = "EVENT_AB";
    @Event public static final String EVENT_BB = "EVENT_BB";
    @Event public static final String EVENT_BC = "EVENT_BC";
    @Event public static final String EVENT_CD = "EVENT_CD";
    
    @Transitions({@Transition(source=STATE_A, target=STATE_B, event=EVENT_AB),
                  @Transition(source=STATE_B, target=STATE_COND, event=EVENT_BC),
                  @Transition(source=STATE_COND, target=STATE_D, event=EVENT_CD)})
    public void noop(TransitionInfo info) {
        System.out.println("#tx: " + info);
    }
    
    @ExitState(STATE_A)
    public Boolean exitA(TransitionInfo info) {
        System.out.println("#exit: " + info);
        return true;
    }
    
    @EnterState(STATE_COND)
    public EventInfo transitionBC(TransitionInfo info) {
        System.out.println("#enter: " + info);
        return new EventInfo(EVENT_CD, null);
    }
    
    @Test
    public void test() throws StateMachineException {
        StateMachine sm = StateMachines.newNonReentrant(this);
        sm.processEvent(EVENT_AB, null);
        sm.processEvent(EVENT_BC, null);
        
        Assert.assertEquals(sm.getCurrentState(), STATE_D);
    }
}