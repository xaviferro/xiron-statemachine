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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shisha.statemachine.annotations.EnterState;
import shisha.statemachine.annotations.ExitState;
import shisha.statemachine.annotations.Transition;
import shisha.statemachine.exceptions.ConstraintException;
import shisha.statemachine.exceptions.EventAlreadyExistsException;
import shisha.statemachine.exceptions.EventNotDefinedException;
import shisha.statemachine.exceptions.StateAlreadyExistsException;
import shisha.statemachine.exceptions.StateMachineDefinitionException;
import shisha.statemachine.exceptions.StateNotDefinedException;
import shisha.statemachine.exceptions.TransitionNotDefinedException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Contains all the data for a state machine. It is not thread-safe
 */
public class StateMachineDefinitionImpl implements StateMachineDefinition {
    private static Logger l = LoggerFactory.getLogger(StateMachineDefinitionImpl.class);

    private String startState;

    private HashMap<String, State> states;
    private HashSet<String> events;

    public StateMachineDefinitionImpl() {
        this.states = Maps.newHashMap();
        this.events = Sets.newHashSet();
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
        checkEventNotNull(event);

        if (events.contains(event))
            throw new EventAlreadyExistsException("Event " + event + " already defined in the state machine");

        events.add(event);
        l.debug("#defineEvent succeed for event id " + event);
    }

    public Set<String> getEvents() {
        return Collections.unmodifiableSet(events);
    }

    public void defineState(String state) throws StateAlreadyExistsException, ConstraintException {
        this.defineState(state, false, false);
    }

    public void defineState(String state, boolean isStart, boolean isFinal) throws StateAlreadyExistsException,
            ConstraintException {

        checkStateNotNull(state);

        if (isStart && startState != null)
            throw new ConstraintException("A state machine can only have one start state." + " Cannot define state "
                    + state + " as start state because " + startState + " was already defined as the one and only");

        if (isStart && isFinal)
            throw new ConstraintException("Cannot define state " + state + " as start and end. It does not make sense");

        if (states.containsKey(state)) {
            throw new StateAlreadyExistsException("State " + state + " already defined");
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
        ArrayList<String> result = new ArrayList<String>();
        for (State state : this.states.values()) {
            if (state.isFinal) {
                result.add(state.getName());
            }
        }
        return result;
    }

    private State checkStateExists(String state) throws StateNotDefinedException {
        if (!isState(state))
            throw new StateNotDefinedException("State " + state + " does not exist");

        return states.get(state);
    }

    private void checkStateNotNull(String state) {
        if (state == null)
            throw new IllegalArgumentException("Can not define a state with null value");
    }

    private void checkEventExists(String event) throws EventNotDefinedException {
        if (!isEvent(event))
            throw new EventNotDefinedException("Event " + event + " does not exist");
    }

    private void checkEventNotNull(String event) {
        if (event == null)
            throw new IllegalArgumentException("Can not define an event with null value");
    }

    void defineTransition(Transition transition, final Method method, final Object callee)
            throws StateMachineDefinitionException {
        this.defineTransition(transition.source(), transition.event(), transition.target(), new TransitionController() {
            public void execute(TransitionInfo event) {
                try {
                    method.invoke(callee, event);
                } catch (IllegalAccessException e) {
                    l.error("This should never happen");
                } catch (IllegalArgumentException e) {
                    l.error("This should never happen");
                } catch (InvocationTargetException swallow) {
                    l.error("Exceptions should be treated in the controller. Swallowing it", swallow);
                }
            }
        });
    }

    public void defineTransition(String source, String event, String target, TransitionController controller)
            throws StateMachineDefinitionException {
        State sourceState = checkStateExists(source);
        checkStateExists(target);
        checkEventExists(event);

        if (sourceState.isFinal() && !source.equals(target))
            throw new ConstraintException("Cannot create transitions from the final state " + source);
        sourceState.setTransitionController(event, target, controller);
    }

    void defineExitState(ExitState ann, final Method method, final Object callee)
            throws StateMachineDefinitionException {
        this.defineExitState(ann.value(), new ExitStateController() {
            public Boolean execute(TransitionInfo event) {
                Boolean result = null;
                try {
                    result = (Boolean) method.invoke(callee, event);
                } catch (IllegalAccessException e) {
                    l.error("This should never happen");
                } catch (IllegalArgumentException e) {
                    l.error("This should never happen");
                } catch (InvocationTargetException swallow) {
                    l.error("Exceptions should be treated in the controller. Swallowing it", swallow);
                }
                return result;
            }
        });
    }

    public void defineExitState(String state, ExitStateController controller) throws StateMachineDefinitionException {
        State internalState = checkStateExists(state);
        internalState.setExitStateController(controller);
    }

    void defineEnterState(final EnterState ann, final Method method, final Object callee)
            throws StateMachineDefinitionException {
        this.defineEnterState(ann.value(), new EnterStateController() {
            public EventInfo execute(TransitionInfo event) {
                EventInfo evtInfo = null;
                try {
                    evtInfo = (EventInfo) method.invoke(callee, event);
                } catch (IllegalAccessException e) {
                    l.error("This should never happen");
                } catch (IllegalArgumentException e) {
                    l.error("This should never happen");
                } catch (InvocationTargetException swallow) {
                    l.error("Exceptions should be treated in the controller. Swallowing it", swallow);
                }
                return evtInfo;
            }
        });
    }

    public void defineEnterState(String state, EnterStateController controller) throws StateMachineDefinitionException {
        State internalState = checkStateExists(state);
        internalState.setEnterStateController(controller);
    }

    public TransitionController getTransitionController(String state, String event) throws StateNotDefinedException,
            EventNotDefinedException, TransitionNotDefinedException {
        TransitionController controller = null;
        State internalState = checkStateExists(state);
        if (internalState != null)
            controller = internalState.getTransitionController(event);

        return controller;
    }

    public EnterStateController getEnterStateController(String state) throws StateNotDefinedException {
        EnterStateController controller = null;
        State internalState = checkStateExists(state);
        if (internalState != null)
            controller = internalState.getEnterStateController();

        return controller;
    }

    public ExitStateController getExitStateController(String state) throws StateNotDefinedException {
        ExitStateController controller = null;
        State internalState = checkStateExists(state);
        if (internalState != null)
            controller = internalState.getExitStateController();

        return controller;
    }

    /**
     * This method is only invoked for valid source states, so no additional
     * checks are required.
     * 
     * @throws TransitionNotDefinedException
     *             in case the transition does not exist
     */
    public String getTargetState(String source, String event) throws TransitionNotDefinedException,
            StateNotDefinedException {
        State src = checkStateExists(source);

        HashMap<String, TransitionTarget> txs = src.getTransitions();
        TransitionTarget target = txs.get(event);
        if (target == null)
            throw new TransitionNotDefinedException("Transition from state " + source + " with event " + event
                    + " not defined");

        return target.getState();
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
            HashMap<String, TransitionTarget> transitions = states.get(source).getTransitions();
            for (String key : transitions.keySet())
                result.add(key);
        }

        return result;
    }

    private void printTransitionsForState(State state, StringBuilder sb) {
        String NEWLINE = "\n";
        sb.append("<Transitions>").append(NEWLINE);

        if (state.getExitStateController() != null)
            sb.append("<ExitState state=\"").append(state.getName()).append("\" />").append(NEWLINE);

        HashMap<String, TransitionTarget> txs = state.getTransitions();
        for (String event : txs.keySet()) {
            TransitionTarget target = txs.get(event);
            sb.append("<Transition ").append("source=\"").append(state.getName()).append("\" ").append("event=\"")
                    .append(event).append("\" ").append("target=\"").append(target.getState()).append("\"")
                    .append(" />").append(NEWLINE);
        }

        if (state.getEnterStateController() != null)
            sb.append("<EnterState state=\"").append(state.getName()).append("\" />");

        sb.append("</Transitions>").append(NEWLINE);
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
                sb.append("<FinalState>").append(state).append("</FinalState>").append(NEWLINE);
            } else {
                sb.append("<State>").append(state).append("</State>").append(NEWLINE);
            }
            printTransitionsForState(state, sb);
        }
        sb.append("</States>").append(NEWLINE);

        sb.append("<Events>").append(NEWLINE);
        for (String event : events) {
            sb.append("<Event>").append(event).append("</Event>").append(NEWLINE);
        }
        sb.append("</Events>").append(NEWLINE);

        /*
         * sb.append("<Transitions>").append(NEWLINE); for (State state :
         * states.values()) { HashMap<String, TransitionTarget> txs =
         * state.getTransitions(); for (String event : txs.keySet()) {
         * TransitionTarget target = txs.get(event);
         * sb.append("<Transition ").append
         * ("source=\"").append(state.getName()).
         * append("\" ").append("event=\"")
         * .append(event).append("\" ").append("target=\""
         * ).append(target.getState()).append("\"")
         * .append(" />").append(NEWLINE); } if (state.getEnterStateController()
         * != null)
         * sb.append("<EnterState state=\"").append(state.getName()).append
         * ("\" />"); if (state.getExitStateController() != null)
         * sb.append("<ExitState state=\""
         * ).append(state.getName()).append("\" />"); }
         * sb.append("</Transitions>").append(NEWLINE);
         */

        sb.append("</StateMachineDefinition>");
        return sb.toString();
    }

    private class TransitionTarget {
        private String state;
        private TransitionController transitionController;

        public TransitionTarget(String state, TransitionController transitionController) {
            super();
            this.state = state;
            this.transitionController = transitionController;
        }

        public String getState() {
            return state;
        }

        public TransitionController getTransitionController() {
            return transitionController;
        }
    }

    /**
     * Contains all state related info. The name, whether the state is final or
     * not and the list of transitions to other states.
     */
    private class State {
        private String name;
        private boolean isStart;
        private boolean isFinal;
        private EnterStateController enterStateController;
        private ExitStateController exitStateController;

        private HashMap<String, TransitionTarget> transitions;

        public State(String name, boolean isStart, boolean isFinal) {
            this.name = name;
            this.isStart = isStart;
            this.isFinal = isFinal;
            this.transitions = new HashMap<String, TransitionTarget>();
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

        public void setEnterStateController(EnterStateController enterStateController) {
            this.enterStateController = enterStateController;
        }

        public EnterStateController getEnterStateController() {
            return this.enterStateController;
        }

        public ExitStateController getExitStateController() {
            return exitStateController;
        }

        public void setExitStateController(ExitStateController exitStateController) {
            this.exitStateController = exitStateController;
        }

        public void setTransitionController(String event, String target, TransitionController controller) {
            if (!transitions.containsKey(event))
                transitions.put(event, new TransitionTarget(target, controller));
        }

        public TransitionController getTransitionController(String event) {
            TransitionController controller = null;
            TransitionTarget info = this.transitions.get(event);
            if (info != null)
                controller = info.getTransitionController();

            return controller;
        }

        public HashMap<String, TransitionTarget> getTransitions() {
            return this.transitions;
        }

        public String toString() {
            return name;
        }
    }
}
