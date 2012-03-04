package net.xiron.pattern.statemachine.annotated;


/**
 * The state is not final
 * 
 * @author xavi.ferro
 */
public class IllegalStateAnnotation_01 implements AnnotatedController {
    @StartState @State public static String STATE_A = "STATE_A";
}
