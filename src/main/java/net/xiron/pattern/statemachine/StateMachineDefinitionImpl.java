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
package net.xiron.pattern.statemachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all the data for a state machine. It is not thread-safe
 */
public class StateMachineDefinitionImpl implements StateMachineDefinition {
    private static Logger l = LoggerFactory
            .getLogger(StateMachineDefinitionImpl.class);

    private String startState;

    private HashMap<String, HashMap<String, String>> states;
    private HashSet<String> events;

    public StateMachineDefinitionImpl() {
        this.states = new HashMap<String, HashMap<String, String>>();
        this.events = new HashSet<String>();
    }

    @Override
    public boolean isEvent(String event) {
        return this.events.contains(event);
    }

    @Override
    public boolean isState(String state) {
        return this.states.containsKey(state);
    }

    @Override
    public void defineEvent(String event) {
        if (event == null)
            throw new IllegalArgumentException(
                    "Can not define an event with null value");

        events.add(event);
        if (l.isDebugEnabled())
            l.debug("#defineEvent succeed for event id " + event);
    }

    @Override
    public List<String> getEvents() {
        ArrayList<String> copy = new ArrayList<String>();
        for (String evt : events)
            copy.add(evt);

        return copy;
    }

    @Override
    public void defineState(String state) {
        if (state == null)
            throw new IllegalArgumentException(
                    "Can not define a state with null value");

        if (!states.containsKey(state))
            states.put(state, new HashMap<String, String>());

        if (l.isDebugEnabled())
            l.debug("#defineState succeed for state id " + state);
    }

    @Override
    public void setStartState(String state) throws StateNotDefinedException {
        if (!states.containsKey(state))
            throw new StateNotDefinedException("Can not set start state to "
                    + state + " as is not already defined");

        startState = state;

        if (l.isDebugEnabled())
            l.debug("#setStartState succeed with state id " + state);
    }
    
    @Override
    public String getStartState() {
        return this.startState;
    }

    @Override
    public void defineTransition(String sourceState, String targetState,
                                 String event)
            throws StateNotDefinedException, EventNotDefinedException {
        if (!states.containsKey(sourceState))
            throw new StateNotDefinedException(
                    "Cannot define a transition for a source state "
                            + sourceState + " that doesn't exist");

        if (!states.containsKey(targetState))
            throw new StateNotDefinedException(
                    "Cannot define a transition for a target state "
                            + targetState + " that doesn't exist");

        if (!events.contains(event))
            throw new StateNotDefinedException(
                    "Cannot define a transition for an event " + event
                            + " that doesn't exist");

        HashMap<String, String> transitions = states.get(sourceState);
        if (!transitions.containsKey(event))
            transitions.put(event, targetState);
    }

    /**
     * This method is only invoked for valid source states, so no additional
     * checks are required.
     * 
     * @throws TransitionNotDefinedException
     *             in case the transition does not exist
     */
    @Override
    public String getTargetState(String source, String event)
            throws TransitionNotDefinedException {
        HashMap<String, String> txs = this.states.get(source);

        String target = txs.get(event);
        if (target == null)
            throw new TransitionNotDefinedException("Transition from state "
                    + source + " with event " + event + " not defined");

        return target;
    }

    @Override
    public void setEndState(String state) {
        throw new Error("setEndState not defined");// TODO Auto-generated method
                                                   // stub

    }

    @Override
    public List<String> getStates() {
        ArrayList<String> result = new ArrayList<String>();
        for (String key : states.keySet())
            result.add(key);
        return result;
    }

    @Override
    public List<String> getEvents(String source) {
        List<String> result = Collections.emptyList();

        if (this.isState(source)) {
            HashMap<String, String> events = this.states.get(source);
            for (String key : events.keySet())
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
        sb.append("<StateMachine");
        if (startState != null)
            sb.append(" startState=\"").append(startState).append("\"");

        sb.append(">").append(NEWLINE);

        sb.append("<States>").append(NEWLINE);
        for (String state : states.keySet()) {
            sb.append("<State>").append(state).append("</State>")
                    .append(NEWLINE);
        }
        sb.append("</States>").append(NEWLINE);

        sb.append("<Events>").append(NEWLINE);
        for (String event : events) {
            sb.append("<Event>").append(event).append("</Event>")
                    .append(NEWLINE);
        }
        sb.append("</Events>").append(NEWLINE);

        sb.append("<Transitions>").append(NEWLINE);
        for (String source : states.keySet()) {
            HashMap<String, String> txs = states.get(source);
            for (String event : txs.keySet()) {
                String target = txs.get(event);
                sb.append("<Transition ").append("source=\"").append(source)
                        .append("\" ").append("event=\"").append(event)
                        .append("\" ").append("target=\"").append(target)
                        .append("\"").append(" />").append(NEWLINE);
            }
        }
        sb.append("</Transitions>").append(NEWLINE);

        sb.append("</StateMachine>");
        return sb.toString();
    }
}
