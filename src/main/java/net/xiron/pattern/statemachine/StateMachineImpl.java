package net.xiron.pattern.statemachine;

import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

/**
 * @Lock{this}
 * 
 * @author xavi.ferro
 */
public class StateMachineImpl implements StateMachine {
    private static Logger l = LoggerFactory.getLogger(StateMachineImpl.class);
    
    private String startState;
    private String currentState;
    /** Whether we are in a transition or not */
    private boolean inTransition = false;
    
    private StateMachineController controller;
    
	private HashMap<String, HashMap<String,String>> states;
	private HashSet<String> events;
	
	public StateMachineImpl() {
		this.states = new HashMap<String, HashMap<String,String>> ();
		this.events = new HashSet<String> ();
	}
	
	@Override public boolean allowsReentrantTransitions() {
	    return false;
	}
	
	@Override public void defineEvent(String event) {
	    if (event == null)
	        throw new IllegalArgumentException("Can not define an event with null value");
	    
	    events.add(event);
	    if (l.isDebugEnabled())
	        l.debug("#defineEvent succeed for event id " + event);
	}
	
	@Override public void defineState(String state) {
	    if (state == null)
            throw new IllegalArgumentException("Can not define a state with null value");
	    
	    if (!states.containsKey(state))
	        states.put(state, new HashMap<String, String>());
	    
	    if (l.isDebugEnabled())
            l.debug("#defineState succeed for state id " + state);
	}
	
	@Override public void setStartState(String state) throws StateNotDefinedException {
        if (!states.containsKey(state))
            throw new StateNotDefinedException("Can not set start state to " + state + " as is not already defined");
        
        startState = state;
        currentState = startState;
        
        if (l.isDebugEnabled())
            l.debug("#setStartState succeed with state id " + state);
    }

    @Override public void defineTransition(String sourceState, String targetState, String event) 
        throws StateNotDefinedException, EventNotDefinedException 
    {
        if (!states.containsKey(sourceState))
            throw new StateNotDefinedException("Cannot define a transition for a source state " + sourceState + " that doesn't exist");
        
        if (!states.containsKey(targetState))
            throw new StateNotDefinedException("Cannot define a transition for a target state " + targetState + " that doesn't exist");
        
        if (!events.contains(event))
            throw new StateNotDefinedException("Cannot define a transition for an event " + event + " that doesn't exist");
        
        HashMap<String,String> transitions = states.get(sourceState);
        if (!transitions.containsKey(event))
            transitions.put(event, targetState);
    }

    /**
     * This method is only invoked for valid source states, so no additional checks are required.
     * 
     * @throws TransitionNotDefinedException in case the transition does not exist
     */
    private String getTargetState(String source, String event) throws TransitionNotDefinedException {
        HashMap<String, String> txs = this.states.get(source);
        
        String target = txs.get(event);
        if (target == null)
            throw new TransitionNotDefinedException("Transition from state " + source + " with event " + event + " not defined");
        
        return target;
    }
    
    /*
     * TODO. We could extract this functionality and apply a Strategy pattern. It would allow
     * us to have different implementations (NonReentrant, Reentrant -with a queue of things-),
     * or just set up a flag when starting. For the time being, we keep the NonReentrant one.
     * 
     * Allowing the user to choose between strategies might impact in the interface as this
     * method -returns a boolean- wouldn't make much sense (sometimes the transition would be enqueued)
     * 
     * @see net.xiron.pattern.statemachine.StateMachine#processEvent(java.lang.String, java.lang.Object)
     */
    @Override public synchronized void processEvent(String event, Object object)
        throws ReentrantTransitionNotAllowed, EventNotDefinedException, TransitionNotDefinedException 
    {
        if (!events.contains(event))
            throw new EventNotDefinedException("Event " + event + " not defined");
        
        if (inTransition) {
            /*
             * We do not need to check whether the sm allows reentrance or not. If inTransition
             * has been set to true, it is reentrant -we won't never modify its value though-.
             */
            throw new ReentrantTransitionNotAllowed("Reentrance from the same thread is not allowed");
        }
        else {
            if (!allowsReentrantTransitions()) {
                inTransition = true;
            } else {
                // TODO. Do the reentrant approach!
            }
        }
        
        try {
            String source = currentState;
            String target = getTargetState(source, event);
            TransitionEvent tEvent = new TransitionEvent(source, event, target, object);
            
            if (controller.phaseExitState(tEvent)) {
                controller.phaseTransition(tEvent);
                currentState = target;
                PhaseEnterResult result = controller.phaseEnterState(tEvent);
                if (result != null) {
                    l.debug("#processEvent: Redirecting forced by controller to event " + result.getEvent());
                    if (!allowsReentrantTransitions())
                        inTransition = false; // Unnecessary check, coz inTransition=false works fine for reentrant and non-reentran
                    
                    this.processEvent(result.getEvent(), result.getObject());
                }
            } else {
                if (l.isDebugEnabled())
                    l.debug("#processEvent: transition cancelled on exit state phase");
            }
        } finally {
            if (!allowsReentrantTransitions()) {
                inTransition = false;
            }
        }
    }
	
    @Override public void setController(StateMachineController controller) {
		this.controller = controller;
	}
    
    @Override public synchronized String getCurrentState() {
        return this.currentState;
    }
    
    /**
     * Returns the state machine definition in a XML format. This is not
     * a cheap operation.
     */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        String NEWLINE = "\n";
        sb.append("<StateMachine");
        if (startState != null)
            sb.append(" startState=\"").append(startState).append("\"");
        
        sb.append(">").append(NEWLINE);
        
        sb.append("<States>").append(NEWLINE);
        for (String state:states.keySet()) {
            sb.append("<State>").append(state).append("</State>").append(NEWLINE);
        }
        sb.append("</States>").append(NEWLINE);
        
        sb.append("<Events>").append(NEWLINE);
        for (String event:events) {
            sb.append("<Event>").append(event).append("</Event>").append(NEWLINE);
        }
        sb.append("</Events>").append(NEWLINE);
        
        sb.append("<Transitions>").append(NEWLINE);
        for (String source:states.keySet()) {
            HashMap<String,String> txs = states.get(source);
            for (String event:txs.keySet()) {
                String target = txs.get(event);
                sb.append("<Transition ")
                    .append("source=\"").append(source).append("\" ")
                    .append("event=\"").append(event).append("\" ")
                    .append("target=\"").append(target).append("\"")
                    .append(" />").append(NEWLINE);
            }
        }
        sb.append("</Transitions>").append(NEWLINE);
        
        sb.append("</StateMachine>");
        return sb.toString();
    }
}
