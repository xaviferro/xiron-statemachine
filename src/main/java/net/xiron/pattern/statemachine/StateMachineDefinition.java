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

import java.util.List;

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

/**
 * Allows defining a set of states that are connected to each other. State
 * machines process events that might fire transitions between states.
 * 
 * <p>
 * There are many reasons why this pattern is very useful. Keeping the right
 * state for an object can be tedious and error-prone if not using the right
 * mechanism specially in highly concurrent systems.
 * 
 * <p>
 * Constraints and invariants.
 * <ul>
 * <li>We are forced to declare explicitly states and events. We could try to
 * use a less explicit model, but we want to avoid typing and maintenance
 * errors.</li>
 * <li>We are forced to declare transitions. A transition is a set of a source
 * state, target state and the event that provokes the transition itself.</li>
 * <li>Each transition is executed in 3 steps that allow us to keep a good
 * control of the actions to perform in each step. Check
 * {@link TransitionController} for further details.</li>
 * <li>State machines are designed to protect critical sections in a complex
 * event driven environment. Only one thread CAN execute a transition at a time.
 * Any manipulation of sensitive information SHOULD be done during a transition.
 * </li>
 * <li>So, during a transition the lock of the object is acquired and it won't
 * be released until the transition finishes. Be aware of that because it might
 * cause deadlocks if you are not a good programmer :-)</li>
 * <li>Using the lock guarantees no other thread will be in the critical
 * section. But, what about the same thread? It might be possible to process an
 * event while processing another event. We want to avoid that because it might
 * cause inconsistencies. So, if we define the flag
 * {@link #allowsReentrantTransitions} to false we are forcing the state machine
 * to prevent that situation.</li>
 * <li>If {@link #allowsReentrantTransition} is set to false -the only one
 * supported now-, we are forcing the state machine to guarantee that one and
 * only thread is allowed to perform transitions at a time. The same thread is
 * not allowed to perform more transitions during the transition. You might use
 * the {@link TransitionController#phaseEnterState} mechanism for forwarding</li>
 * </ul>
 * 
 * <p>
 * Invariant.
 * <ul>
 * <li>We only allow one transition at a time within the lock.</li>
 * <li>Transitions from other threads with be blocked by the state machine lock.
 * </li>
 * <li>Transitions from the same thread will throw an exception as we need to
 * avoid recurrent transitions that might end up in an error state. For example,
 * we want to prevent transitions during the exit phase and consequent
 * notifications out of order.</li>
 * </ul>
 */
public interface StateMachineDefinition {

    /**
     * We need to define the state in order to define transitions later on.
     * Otherwise, we would get an exception
     */
    public void defineState(String state);

    /**
     * Is it an already define state?
     */
    public boolean isState(String state);

    /**
     * Returns a copy of the list of strings
     */
    public List<String> getStates();

    /**
     * Select the state where the state machine starts
     * 
     * @throws StateNotDefinedException
     *             in case the state hasn't been previously defined using the
     *             {@link #defineState(state)} method
     */
    public void setStartState(String state) throws StateNotDefinedException;
    public void setEndState(String state) throws StateNotDefinedException;
    
    public String getStartState();
    
    /**
     * We need to define the event in order to define transitions later on.
     * Otherwise, we would get an exception
     */
    public void defineEvent(String event);

    /**
     * Is it an already defined event?
     */
    public boolean isEvent(String event);

    /**
     * Returns a copy of the list of all events defined in the state machine
     */
    public List<String> getEvents();

    /**
     * Returns a copy of all the events that could be applied to
     * <code>state</code>
     */
    public List<String> getEvents(String state);

    /**
     * Defining a transition. We must explicitly do it, otherwise any event that
     * provokes a transition that is not defined will raise an exception
     */
    public void defineTransition(String sourceState, String targetState,
                                 String event)
            throws StateNotDefinedException, EventNotDefinedException;

    /**
     * Returns the state we reach for the specified source state and event
     */
    public String getTargetState(String source, String event)
            throws TransitionNotDefinedException;
}
