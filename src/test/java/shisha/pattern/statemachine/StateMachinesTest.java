package shisha.pattern.statemachine;

import org.testng.annotations.Test;

import shisha.pattern.statemachine.util.LegalStateMachineDefinition;
import shisha.statemachine.StateMachine;
import shisha.statemachine.StateMachines;
import shisha.statemachine.exceptions.StateMachineDefinitionException;

public class StateMachinesTest {
    @Test
    public void testAnnotatedStateMachineLoadedProperly() throws StateMachineDefinitionException {
        StateMachine stateMachine = StateMachines.newNonReentrant(new LegalStateMachineDefinition());
        System.out.println(stateMachine.toString());
    }
}
