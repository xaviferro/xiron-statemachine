package shisha.statemachine.annotations.util;

import shisha.statemachine.EventInfo;
import shisha.statemachine.TransitionInfo;
import shisha.statemachine.annotations.AStateMachine;
import shisha.statemachine.annotations.EnterState;
import shisha.statemachine.annotations.Event;
import shisha.statemachine.annotations.State;
import shisha.statemachine.annotations.Transition;
import shisha.statemachine.annotations.Transitions;

@AStateMachine
public class StateMachineWithRedirectInEnterPhase {
    @State(isStart=true) public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    @State public static final String STATE_COND = "STATE_COND";
    @State public static final String STATE_D = "STATE_D";
    
    @Event public static final String EVENT_AB = "EVENT_AB";
    @Event public static final String EVENT_BB = "EVENT_BB";
    @Event public static final String EVENT_BC = "EVENT_BC";
    @Event public static final String EVENT_CD = "EVENT_CD";
    
    @Transitions({@Transition(source=STATE_A, target=STATE_B, event=EVENT_AB),
                  @Transition(source=STATE_COND, target=STATE_D, event=EVENT_CD)})
    public void noop(TransitionInfo tEvent) {}
    
    @EnterState(STATE_COND)
    public EventInfo transitionBC(TransitionInfo tEvent) {
        return new EventInfo(EVENT_CD, null);
    }
}
