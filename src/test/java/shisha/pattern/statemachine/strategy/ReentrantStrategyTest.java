package shisha.pattern.statemachine.strategy;


import org.testng.annotations.BeforeMethod;

import shisha.statemachine.annotations.AnnotatedControllerFactory;
import shisha.statemachine.annotations.AnnotatedControllerProcessor;
import shisha.statemachine.annotations.Event;
import shisha.statemachine.annotations.State;
import shisha.statemachine.exceptions.IllegalAnnotationException;
import shisha.statemachine.exceptions.StateMachineException;

public class ReentrantStrategyTest {
	@State(isStart=true) public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    @State public static final String STATE_C = "STATE_C";
    @State(isFinal=true) public static final String STATE_D = "STATE_D";
    
    @Event public static final String EVENT_AB = "EVENT_AB";
    @Event public static final String EVENT_BC = "EVENT_BC";
    @Event public static final String EVENT_CB = "EVENT_CB";
    @Event public static final String EVENT_BD = "EVENT_BD";
    @Event public static final String EVENT_CD = "EVENT_CD";
    
    private AnnotatedControllerProcessor processor;
    
    @BeforeMethod
    public void beforeMethod() throws StateMachineException, IllegalAnnotationException {
    	AnnotatedControllerFactory f = new AnnotatedControllerFactory();
    	processor = f.createEnqueueProcessor(this);
    }
}
