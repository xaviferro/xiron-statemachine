package shisha.statemachine;

import shisha.statemachine.impl.NonReentrantStateMachine;
import shisha.statemachine.impl.ReentrantStateMachine;

public class StateMachines {
    public StateMachine createReentrantStateMachine(StateMachineDefinition definition, StateMachineController ... controllers) {
        return new ReentrantStateMachine(definition);
    }
    
    public StateMachine createNonReentrantStateMachine(StateMachineDefinition definition, StateMachineController ... controllers) {
        return new NonReentrantStateMachine(definition);
    }
}
