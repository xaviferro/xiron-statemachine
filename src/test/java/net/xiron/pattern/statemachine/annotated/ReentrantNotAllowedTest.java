package net.xiron.pattern.statemachine.annotated;

import java.lang.reflect.InvocationTargetException;

import junit.framework.Assert;

import net.xiron.pattern.statemachine.PhaseEnterResult;
import net.xiron.pattern.statemachine.TransitionEvent;
import net.xiron.pattern.statemachine.exceptions.StateMachineException;

import org.junit.Test;

public class ReentrantNotAllowedTest implements AnnotatedController {
    @State @StartState public static final String STATE_A = "STATE_A";
    @State public static final String STATE_B = "STATE_B";
    @State public static final String STATE_C = "STATE_C";
    
    @Event public static final String EVENT_AB = "EVENT_AB";
    @Event public static final String EVENT_BC = "EVENT_BC";
    
    private AnnotatedControllerProcessor processor;
    
    @Transition(source=STATE_A,target=STATE_B,event=EVENT_AB,phase=TransitionPhases.PHASE_ENTER) 
    public PhaseEnterResult transitionAB(TransitionEvent evnt) throws StateMachineException {
        processor.processEvent(EVENT_BC, null);
        return null;
    }
    
    @Test
    public void testReentrant() throws StateMachineException {
        processor = new AnnotatedControllerProcessor(this);
        processor.processEvent(EVENT_AB, null);
        Assert.assertEquals(processor.getStateMachine().getCurrentState(), STATE_B);
    }
}
