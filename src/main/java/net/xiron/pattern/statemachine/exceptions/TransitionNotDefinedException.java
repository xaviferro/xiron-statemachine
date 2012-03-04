package net.xiron.pattern.statemachine.exceptions;

public class TransitionNotDefinedException extends StateMachineException {
    private static final long serialVersionUID = 1L;
    
    public TransitionNotDefinedException(String msg) {
        super(msg);
    }
}
