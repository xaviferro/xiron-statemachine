package net.xiron.pattern.statemachine.exceptions;

public class ReentrantTransitionNotAllowed extends StateMachineException {
    private static final long serialVersionUID = 1L;
    public ReentrantTransitionNotAllowed(String msg) {
        super(msg);
    }
}
