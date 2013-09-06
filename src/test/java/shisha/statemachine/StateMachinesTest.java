package shisha.statemachine;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import shisha.statemachine.annotations.util.IllegalEventAnnotationNotFinal;
import shisha.statemachine.annotations.util.IllegalEventAnnotationNotPublic;
import shisha.statemachine.annotations.util.IllegalStateAnnotationNotAString;
import shisha.statemachine.annotations.util.IllegalStateDefinitionNotFinal;
import shisha.statemachine.annotations.util.IllegalTransitionAnnotationWrongParameter;
import shisha.statemachine.annotations.util.StateMachineWithEnterStateMethodReturningVoid;
import shisha.statemachine.annotations.util.StateMachineWithExitStateReturningVoid;
import shisha.statemachine.annotations.util.StateMachineWithNoStartState;
import shisha.statemachine.annotations.util.StateMachineWithRedirectInEnterPhase;
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
        StateMachine stateMachine = StateMachines.newNonReentrant(new StateMachineWithRedirectInEnterPhase());
        System.out.println(stateMachine.toString());
    }

    @Test
    public void testAnnotatedReentrantStateMachine() throws StateMachineException {
        StateMachine stateMachine = StateMachines.newReentrant(new StateMachineWithRedirectInEnterPhase());
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
    public void testEventAnnotatedFieldIsNotFinal() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalEventAnnotationNotFinal());
    }

    @Test(expectedExceptions = IllegalStateAnnotationException.class)
    public void testStateAnnotatedFieldIsPublicStaticButNotFinal() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalStateDefinitionNotFinal());
    }

    @Test(expectedExceptions = IllegalStateAnnotationException.class)
    public void testStateAnnotatedFieldNotAString() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalStateAnnotationNotAString());
    }

    @Test(expectedExceptions = IllegalTransitionAnnotationException.class)
    public void testTransitionAnnotatedMethodHasWrongParameterType() throws StateMachineDefinitionException, IllegalAnnotationException {
        StateMachines.newNonReentrant(new IllegalTransitionAnnotationWrongParameter());
    }

    @Test(expectedExceptions = IllegalEventAnnotationException.class)
    public void testEventAnnotatedFieldIsNotPublic() throws StateMachineException {
        StateMachines.newNonReentrant(new IllegalEventAnnotationNotPublic());
    }
    
    @Test
    public void testRedirectOnEnterState() throws StateMachineException {
        StateMachine sm = StateMachines.newNonReentrant(new StateMachineWithRedirectOnEnterPhase());
        sm.processEvent(StateMachineWithRedirectOnEnterPhase.EVENT_AB, null);
        sm.processEvent(StateMachineWithRedirectOnEnterPhase.EVENT_BC, null);
        
        assertEquals(sm.getCurrentState(), StateMachineWithRedirectOnEnterPhase.STATE_D);
    }
    
    @Test
    public void testEnterStateAnnotatedMethodNotReturningAnEventInfo() throws StateMachineException {
        StateMachine sm = StateMachines.newNonReentrant(new StateMachineWithEnterStateMethodReturningVoid());
        sm.processEvent(StateMachineWithEnterStateMethodReturningVoid.EVENT_AB, null);
        
        assertEquals(sm.getCurrentState(), StateMachineWithEnterStateMethodReturningVoid.STATE_B);
    }
    
    @Test
    public void testExitStateAnnotatedMethodNotReturningABoolean() throws StateMachineException {
        StateMachine sm = StateMachines.newNonReentrant(new StateMachineWithExitStateReturningVoid());
        sm.processEvent(StateMachineWithEnterStateMethodReturningVoid.EVENT_AB, null);
        
        assertEquals(sm.getCurrentState(), StateMachineWithEnterStateMethodReturningVoid.STATE_B);
    }
}
