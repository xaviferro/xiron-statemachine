package net.xiron.pattern.statemachine.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Transition annotation allows the AnnotatedControllerProcessor to look for
 * methods that are marked with it. 
 * 
 * @author xavi.ferro 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Transition {
    /**
     * One might be interested in a specific phase of the transition to 
     * perform the operation. Check the @link{StateMachineController} for 
     * further information.
     * 
     * By default, its value is @link{TransitionPhases.PHASE_TRANSITION}.
     */
    TransitionPhases phase() default TransitionPhases.PHASE_TRANSITION;
    
    /**
     * The state which we came from
     */
    String source();
    
    /**
     * The state which we are going to
     */
    String target();
    
    /**
     * The event that provokes the transition
     */
    String event();
}
