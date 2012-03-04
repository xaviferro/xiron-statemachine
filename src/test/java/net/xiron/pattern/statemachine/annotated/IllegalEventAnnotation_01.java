package net.xiron.pattern.statemachine.annotated;


/**
 * Event is not final
 * 
 * @author xavi.ferro
 */
public class IllegalEventAnnotation_01 implements AnnotatedController {
    @Event public static String EVENT_AB = "EVENT_AB";
}
