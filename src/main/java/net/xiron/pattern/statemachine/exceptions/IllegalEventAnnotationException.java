package net.xiron.pattern.statemachine.exceptions;

public class IllegalEventAnnotationException extends IllegalAnnotationException {
    private static final long serialVersionUID = 1L;

    public IllegalEventAnnotationException(String msg) {
        super(msg);
    }
}
