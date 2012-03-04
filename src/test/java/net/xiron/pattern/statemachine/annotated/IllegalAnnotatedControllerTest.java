package net.xiron.pattern.statemachine.annotated;

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.IllegalAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalEventAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalStateAnnotationException;
import net.xiron.pattern.statemachine.exceptions.IllegalTransitionAnnotationException;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;

import org.junit.Test;

public class IllegalAnnotatedControllerTest  {
    @Test(expected=IllegalEventAnnotationException.class)
    public void testIllegalEvent_01() throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException {
        AnnotatedControllerProcessor processor = new AnnotatedControllerProcessor(new IllegalEventAnnotation_01());
    }
    
    @Test(expected=IllegalStateAnnotationException.class)
    public void testIllegalState_01() throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException {
        AnnotatedControllerProcessor processor = new AnnotatedControllerProcessor(new IllegalStateAnnotation_01());
    } 
    
    @Test(expected=IllegalStateAnnotationException.class)
    public void testIllegalState_02() throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException {
        AnnotatedControllerProcessor processor = new AnnotatedControllerProcessor(new IllegalStateAnnotation_02());
    } 
    
    @Test(expected=IllegalTransitionAnnotationException.class)
    public void testIllegalTransition_01() throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException {
        AnnotatedControllerProcessor processor = new AnnotatedControllerProcessor(new IllegalTransitionAnnotation_01());
    } 
}
