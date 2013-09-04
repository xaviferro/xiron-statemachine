package shisha.statemachine;

import junit.framework.Assert;

import org.testng.annotations.Test;

import shisha.statemachine.annotations.util.IllegalEventAnnotation_01;
import shisha.statemachine.annotations.util.IllegalEventAnnotation_02;
import shisha.statemachine.annotations.util.IllegalStateAnnotation_01;
import shisha.statemachine.annotations.util.IllegalStateAnnotation_02;
import shisha.statemachine.annotations.util.IllegalTransitionAnnotation_01;
import shisha.statemachine.annotations.util.LegalStateMachineDefinition;
import shisha.statemachine.annotations.util.StateMachineWithNoStartState;
import shisha.statemachine.annotations.util.StateMachineWithRedirectOnEnterPhase;
import shisha.statemachine.exceptions.IllegalAnnotationException;
import shisha.statemachine.exceptions.IllegalControllerAnnotationException;
import shisha.statemachine.exceptions.IllegalEventAnnotationException;
import shisha.statemachine.exceptions.IllegalStateAnnotationException;
import shisha.statemachine.exceptions.IllegalTransitionAnnotationException;
import shisha.statemachine.exceptions.StartStateNotDefinedException;
import shisha.statemachine.exceptions.StateMachineDefinitionException;
import shisha.statemachine.exceptions.StateMachineException;

public class StateMachinesTest {
    @Test
    public void testAnnotatedStateMachineLoadedProperly() throws StateMachineDefinitionException {
        StateMachine stateMachine = StateMachines.newNonReentrant(new LegalStateMachineDefinition());
        System.out.println(stateMachine.toString());
    }

    @Test
    public void testAnnotatedReentrantStateMachine() throws StateMachineException {
        StateMachine stateMachine = StateMachines.newReentrant(new LegalStateMachineDefinition());
        System.out.println(stateMachine.toString());
    }

    @Test(expectedExceptions = IllegalControllerAnnotationException.class)
    public void testNonAnnotatedInstance() throws StateMachineDefinitionException {
        StateMachines.newNonReentrant(new Object());
    }
    
    @Test(expectedExceptions = StartStateNotDefinedException.class)
    public void testStartStateNotDefined() throws StateMachineDefinitionException {
        StateMachines.newNonReentrant(new StateMachineWithNoStartState());
    }
    
    @Test(expectedExceptions = IllegalEventAnnotationException.class)
    public void testIllegalEvent_01() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalEventAnnotation_01());
    }

    @Test(expectedExceptions = IllegalStateAnnotationException.class)
    public void testIllegalState_01() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalStateAnnotation_01());
    }

    @Test(expectedExceptions = IllegalStateAnnotationException.class)
    public void testIllegalState_02() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalStateAnnotation_02());
    }

    @Test(expectedExceptions = IllegalTransitionAnnotationException.class)
    public void testIllegalTransition_01() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalTransitionAnnotation_01());
    }

    @Test(expectedExceptions = IllegalEventAnnotationException.class)
    public void testIllegalEvent_02() throws StateMachineException {
        StateMachines.newNonReentrant(new IllegalEventAnnotation_02());
    }
    
    @Test
    public void testRedirectOnEnterState() throws StateMachineException {
        StateMachine sm = StateMachines.newNonReentrant(new StateMachineWithRedirectOnEnterPhase());
        sm.processEvent(StateMachineWithRedirectOnEnterPhase.EVENT_AB, null);
        sm.processEvent(StateMachineWithRedirectOnEnterPhase.EVENT_BC, null);
        
        Assert.assertEquals(sm.getCurrentState(), StateMachineWithRedirectOnEnterPhase.STATE_D);
    }
}
