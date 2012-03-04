package net.xiron.pattern.statemachine.annotated;


/**
 * Wrong parameter names is transition
 * 
 * @author xavi.ferro
 */
public class IllegalTransitionAnnotation_01 implements AnnotatedController {
    @StartState @State public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    
    @Event public static final String EVENT_AB = "EVENT_AB";
    
    @Transition(source=STATE_A,target=STATE_B,event=EVENT_AB)
    public void t() { }
}
