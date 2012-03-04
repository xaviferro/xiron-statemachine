package net.xiron.pattern.statemachine.exceptions;

public class IllegalAnnotationException extends StateMachineException {
    private static final long serialVersionUID = 1L;
    public IllegalAnnotationException(String msg) {
        super(msg);
    }
}
