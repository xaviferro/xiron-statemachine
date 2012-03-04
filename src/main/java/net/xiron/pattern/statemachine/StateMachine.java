package net.xiron.pattern.statemachine;

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

/**
 * Allows defining a set of states that are connected to each other. State machines
 * process events that might fire transitions between states.
 * 
 * There are many reasons why this pattern is very useful. Keeping the right state
 * for an object can be tedious and error-prone if not using the right mechanism
 * specially in highly concurrent systems.
 * 
 * Constraints and invariants.
 * - We are forced to declare explicitely states and events. We could try to use
 *   a less explicit model, but we want to avoid typing and maintenance errors.
 * - We are forced to declare transitions. A transition is a set of a source state, 
 *   target state and the event that provokes the transition itself.
 * - Each transition is executed in 3 steps that allow us to keep a good control
 *   of the actions to perform in each step. Check @link{StateMachineController} for
 *   further details.
 * - State machines are designed to protect critical sections in a complex event driven
 *   environment. Only one thread CAN execute a transition at a time. Any manipulation
 *   of sensitive information SHOULD be done during a transition.
 * - So, during a transition the lock of the object is acquired and it won't be released 
 *   until the transition finishes. Be aware of that because it might cause deadlocks 
 *   if you are not a good programmer :-)
 * - Using the lock guarantees no other thread will be in the critical section.
 *   But, what about the same thread? It might be possible to process an event while
 *   processing another event. We want to avoid that because it might cause inconsistencies.
 *   So, if we define the flag {@link #allowsReentrantTransitions} to false we are forcing
 *   the state machine to prevent that situation.
 * - If {@link #allowsReentrantTransition} is set to false -the only one supported now-,
 *   we are forcing the state machine to guarantee that one and only thread is allowed
 *   to perform transitions at a time. The same thread is not allowed to perform more
 *   transitions during the transition. You might use the {@link StateMachineController#phaseEnterState}
 *   mechanism for forwarding
 * 
 * Invariant.
 * - We only allow one transition at a time with the lock.
 * - Transitions from other threads with be blocked by the state machine lock.
 * - Transitions from the same thread will throw an exception as we need to avoid recurrent transitions that
 *   might end up in an error state. For example, we want to prevent transitions during the exit phase and
 *   consequent notifications out of order.
 *   
 * @author xavi.ferro
 */
public interface StateMachine {
    /**
     * Allows the thread that owns the lock (that is already executing a transition)
     * to force more transitions out of the {@link StateMachineController#phaseEnterState}
     * phase.
     * 
     * <p>Use a reentrant strategy carefully as it might be more difficult to prevent deadlocks
     * as you might try to call the {@link #processEvent} method from any component that is
     * not the {@link StateMachineController} itself.
     * 
     * <p>TODO. At the moment we don't allow reentrant transitions.
     * 
     * @ return true if we allow reentrant transitions, or false if we want the state
     *          machine to preserve this situation and throw an exception instead. 
     */
    public boolean allowsReentrantTransitions();
    
    /**
     * We need to define the state in order to define transitions later on. Otherwise,
     * we would get an exception
     */
    public void defineState(String state);

    /**
     * Select the state where the state machine starts
     * 
     * @throws StateNotDefinedException in case the state hasn't been previously defined
     *         using the {@link #defineState(state)} method
     */
    public void setStartState(String state) throws StateNotDefinedException;
    
    /**
     * We need to define the event in order to define transitions later on. Otherwise,
     * we would get an exception
     */
    public void defineEvent(String event);

    /**
     * Defining a transition. We must explicitly do it, otherwise any event that
     * provokes a transition that is not defined will raise an exception
     */
    public void defineTransition(String sourceState, String targetState, String event) 
        throws StateNotDefinedException, EventNotDefinedException;

    /**
     * Checks that current event is allowed for the current state. That means that a
     * transition has been defined for this state machine.
     * 
     * If the transition has been defined, this method will perform:
     * 1 - execute the transitionExitPhase
     * 2 - execute the transition
     * 3 - execute the transitionEnterPhase
     * 
     * Everything will happen with the state machine lock acquired, so careful with the
     * deadlocks.
     * 
     * @param event the event that we want to process.
     *  
     * @param object if we need an object to be passed to the controller with
     *        context meaning.
     * 
     * @throws StateNotDefinedExc
     * @throws ReentrantTransitionNotAllowed
     */
    public void processEvent(String event, Object object) 
        throws ReentrantTransitionNotAllowed, EventNotDefinedException, TransitionNotDefinedException;
    
    /**
     * Returns the current state of the state machine.
     */
    public String getCurrentState();
    
    /**
     * Registering the controller that receives notifications when transitions happen.
     * The controller should be the one that modifies the critical section information.
     */
    public void setController(StateMachineController controller);
}
