package net.xiron.pattern.statemachine.annotated;

import net.xiron.pattern.statemachine.PhaseEnterResult;
import net.xiron.pattern.statemachine.TransitionEvent;

public class DumbRedirectController implements AnnotatedController {
    @StartState @State public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    
    @Event public static final String EVENT_AA = "EVENT_AA";
    @Event public static final String EVENT_AB = "EVENT_AB";
    
    @Transition(source=STATE_A,event=EVENT_AA,target=STATE_A,phase=TransitionPhases.PHASE_ENTER)
    public PhaseEnterResult transitionAB(TransitionEvent evt) {
        return new PhaseEnterResult(EVENT_AB, null);
    }
    
    @Transition(source=STATE_A,event=EVENT_AB,target=STATE_B,phase=TransitionPhases.PHASE_TRANSITION)
    public void noop(TransitionEvent evt) {}
}
