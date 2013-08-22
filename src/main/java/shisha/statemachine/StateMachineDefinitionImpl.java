/*  
 * Copyright 2012 xavi.ferro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shisha.statemachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shisha.statemachine.exceptions.ConstraintException;
import shisha.statemachine.exceptions.EventAlreadyExistsException;
import shisha.statemachine.exceptions.EventNotDefinedException;
import shisha.statemachine.exceptions.StateAlreadyExistsException;
import shisha.statemachine.exceptions.StateNotDefinedException;
import shisha.statemachine.exceptions.TransitionNotDefinedException;

/**
 * Contains all the data for a state machine. It is not thread-safe
 */
public class StateMachineDefinitionImpl implements StateMachineDefinition {
    private static Logger l = LoggerFactory
            .getLogger(StateMachineDefinitionImpl.class);

    private String startState;

    private HashMap<String, State> states;
    private HashSet<String> events;

    public StateMachineDefinitionImpl() {
        this.states = new HashMap<String, State>();
        this.events = new HashSet<String>();
    }

    public boolean isEvent(String event) {
        return this.events.contains(event);
    }

    public boolean isState(String state) {
        return this.states.containsKey(state);
    }
    
	public boolean isStartState(String state) {
		boolean result = false;
		
		State s = states.get(state);
		if (s != null)
			result = s.isStart();
		
		return result;
	}

	public boolean isFinalState(String state) {
	    boolean result = false;
	    State s = states.get(state);
        if (s != null)
            result = s.isFinal();
        
        return result;
	}

    public void defineEvent(String event) throws EventAlreadyExistsException {
        if (event == null)
            throw new IllegalArgumentException(
                    "Can not define an event with null value");

        if (events.contains(event))
            throw new EventAlreadyExistsException("Event " + event + " already defined in the state machine");
        
        events.add(event);
        l.debug("#defineEvent succeed for event id " + event);
    }

    public Set<String> getEvents() {
        return Collections.unmodifiableSet(events);
    }

    public void defineState(String state)
            throws StateAlreadyExistsException, ConstraintException {
        this.defineState(state, false, false);
    }

    public void defineState(String state, boolean isStart, boolean isFinal)
            throws StateAlreadyExistsException, ConstraintException {

        if (state == null)
            throw new IllegalArgumentException(
                    "Can not define a state with null value");

        if (isStart && startState != null)
            throw new ConstraintException("A state machine can only have one start state." 
            		+ " Cannot define state " + state
                    + " as start state because " + startState
                    + " was already defined as the one and only");

        if (isStart && isFinal)
            throw new ConstraintException("Cannot define state " + state
                    + " as start and end. It does not make sense");

        if (states.containsKey(state)) {
            throw new StateAlreadyExistsException("State " + state
                    + " already defined");
        } else {
            states.put(state, new State(state, isStart, isFinal));
        }
        
        l.debug("#defineState succeed for state id " + state);

        if (isStart)
            this.startState = state;
    }

    public String getStartState() {
        return this.startState;
    }
    
    public List<String> getFinalStates() {
    	ArrayList<String> result = new ArrayList<String> ();
    	for (State state: this.states.values()) {
    		if (state.isFinal) {
    			result.add(state.getName());
    		}
    	}
    	return result;
    }

    public void defineTransition(String sourceState, String targetState,
                                 String event)
            throws StateNotDefinedException, EventNotDefinedException,
            ConstraintException {

        if (!isState(sourceState))
            throw new StateNotDefinedException(
                    "Cannot define a transition for a source state "
                            + sourceState + " that doesn't exist");

        if (!isState(targetState))
            throw new StateNotDefinedException(
                    "Cannot define a transition for a target state "
                            + targetState + " that doesn't exist");

        if (!isEvent(event))
            throw new EventNotDefinedException(
                    "Cannot define a transition for an event " + event
                            + " that doesn't exist");

        State source = states.get(sourceState);
        if (source.isFinal() && !source.equals(targetState))
            throw new ConstraintException(
                    "Cannot create transitions from the final state "
                            + sourceState);
        source.defineTransition(sourceState, targetState, event);
    }

    /**
     * This method is only invoked for valid source states, so no additional
     * checks are required.
     * 
     * @throws TransitionNotDefinedException
     *             in case the transition does not exist
     */
    public String getTargetState(String source, String event)
            throws TransitionNotDefinedException, StateNotDefinedException {
        State src = this.states.get(source);
        if (src == null)
            throw new StateNotDefinedException("State " + source
                    + " not defined");

        HashMap<String, String> txs = src.getTransitions();
        String target = txs.get(event);
        if (target == null)
            throw new TransitionNotDefinedException("Transition from state "
                    + source + " with event " + event + " not defined");

        return target;
    }

    public List<String> getStates() {
        ArrayList<String> result = new ArrayList<String>();
        for (String key : states.keySet())
            result.add(key);
        return result;
    }

    public List<String> getApplicableEvents(String source) {
        List<String> result = new ArrayList<String>();

        if (this.isState(source)) {
            HashMap<String, String> transitions = states.get(source)
                    .getTransitions();
            for (String key : transitions.keySet())
                result.add(key);
        }

        return result;
    }

    /**
     * Returns the state machine definition in a XML format. This is not a cheap
     * operation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String NEWLINE = "\n";
        sb.append("<StateMachineDefinition");
        if (startState != null)
            sb.append(" startState=\"").append(startState).append("\"");

        sb.append(">").append(NEWLINE);

        sb.append("<States>").append(NEWLINE);
        for (State state : states.values()) {
            if (state.isFinal()) {
                sb.append("<FinalState>").append(state).append("</FinalState>")
                        .append(NEWLINE);
            } else {
                sb.append("<State>").append(state).append("</State>")
                        .append(NEWLINE);
            }
        }
        sb.append("</States>").append(NEWLINE);

        sb.append("<Events>").append(NEWLINE);
        for (String event : events) {
            sb.append("<Event>").append(event).append("</Event>")
                    .append(NEWLINE);
        }
        sb.append("</Events>").append(NEWLINE);

        sb.append("<Transitions>").append(NEWLINE);
        for (State state : states.values()) {
            HashMap<String, String> txs = state.getTransitions();
            for (String event : txs.keySet()) {
                String target = txs.get(event);
                sb.append("<Transition ").append("source=\"")
                        .append(state.getName()).append("\" ")
                        .append("event=\"").append(event).append("\" ")
                        .append("target=\"").append(target).append("\"")
                        .append(" />").append(NEWLINE);
            }
        }
        sb.append("</Transitions>").append(NEWLINE);

        sb.append("</StateMachineDefinition>");
        return sb.toString();
    }

    /**
     * Contains all state related info. The name, whether the state is final or
     * not and the list of transitions to other states.
     */
    private class State {
        private String name;
        private boolean isStart;
        private boolean isFinal;
        private HashMap<String, String> transitions;

        public State(String name, boolean isStart, boolean isFinal) {
            this.name = name;
            this.isStart = isStart;
            this.isFinal = isFinal;
            this.transitions = new HashMap<String, String>();
        }

        public String getName() {
            return this.name;
        }

        public boolean isStart() {
        	return this.isStart;
        }
        
        public boolean isFinal() {
            return this.isFinal;
        }

        public void defineTransition(String sourceState, String targetState,
                                     String event) {
            if (!transitions.containsKey(event))
                transitions.put(event, targetState);
        }

        public HashMap<String, String> getTransitions() {
            return this.transitions;
        }

        public String toString() {
            return name;
        }
    }

}
