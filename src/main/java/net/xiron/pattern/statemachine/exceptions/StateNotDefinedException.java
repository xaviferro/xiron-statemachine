package net.xiron.pattern.statemachine.exceptions;

public class StateNotDefinedException extends StateMachineException {
    private static final long serialVersionUID = 1L;
    
    public StateNotDefinedException(String msg) {
        super(msg);
    }
}
