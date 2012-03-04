package net.xiron.pattern.statemachine.exceptions;

public class IllegalTransitionAnnotationException extends IllegalAnnotationException {
    private static final long serialVersionUID = 1L;
    
    public IllegalTransitionAnnotationException(String msg) {
        super(msg);
    }
}
