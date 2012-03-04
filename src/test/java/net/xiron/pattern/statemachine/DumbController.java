package net.xiron.pattern.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dumb annotated controller useful for testing. Only logs events.
 * One can configure the return value in the phaseExit in the constructor.
 * It does not redirect on the exit phase.
 * 
 * @author xavi.ferro
 */
public class DumbController implements StateMachineController {
    private boolean phaseExit;
    private Logger l = LoggerFactory.getLogger(DumbController.class);
    
    public DumbController(boolean phaseExit) {
        this.phaseExit = phaseExit;
    }
    
    @Override
    public boolean phaseExitState(TransitionEvent evt) {
        if (l.isDebugEnabled())
            l.debug("#phaseExitState: " + evt.getSource() + " + " + evt.getEvent() + " -> " + evt.getTarget());
        
        return phaseExit;
    }

    @Override
    public void phaseTransition(TransitionEvent evt) {
        if (l.isDebugEnabled())
            l.debug("#phaseTransition: " + evt.getSource() + " + " + evt.getEvent() + " -> " + evt.getTarget());
    }

    @Override
    public PhaseEnterResult phaseEnterState(TransitionEvent evt) {
        if (l.isDebugEnabled())
            l.debug("#phaseEnterState: " + evt.getSource() + " + " + evt.getEvent() + " -> " + evt.getTarget());
        
        return null;
    }
}
