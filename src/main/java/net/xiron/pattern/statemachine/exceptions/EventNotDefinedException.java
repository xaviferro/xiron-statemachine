package net.xiron.pattern.statemachine.exceptions;

public class EventNotDefinedException extends StateMachineException {
    private static final long serialVersionUID = 1L;
    
    public EventNotDefinedException(String msg) {
        super(msg);
    }
}
