package shisha.pattern.statemachine;

import org.testng.annotations.Test;

import shisha.pattern.statemachine.util.LegalStateMachineDefinition;
import shisha.statemachine.StateMachine;
import shisha.statemachine.StateMachines;
import shisha.statemachine.exceptions.IllegalControllerAnnotationException;
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
}
