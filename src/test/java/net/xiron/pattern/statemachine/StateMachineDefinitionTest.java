package net.xiron.pattern.statemachine;

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.StateMachineException;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

import org.junit.Test;

public class StateMachineDefinitionTest {
    public static String STATE_A = "STATE_A";
    public static String STATE_B = "STATE_B";
    public static String STATE_C = "STATE_C";
    
    public static String EVENT_AB = "EVENT_AB";
    public static String EVENT_BC = "EVENT_BC";
    public static String EVENT_BB = "EVENT_BB";
    public static String EVENT_BA = "EVENT_BA";
    public static String EVENT_CC = "EVENT_CC";
    
    private StateMachine theInstance;
    
    private StateMachine createStateMachine() 
        throws StateNotDefinedException, EventNotDefinedException 
    {
        if (theInstance == null) {
            theInstance = new StateMachineImpl();
            theInstance.defineEvent(EVENT_AB);
            theInstance.defineEvent(EVENT_BC);
            theInstance.defineEvent(EVENT_BB);
            theInstance.defineEvent(EVENT_BA);
            
            theInstance.defineState(STATE_A);
            theInstance.defineState(STATE_B);
            theInstance.defineState(STATE_C);
            
            theInstance.defineTransition(STATE_A, STATE_B, EVENT_AB);
            theInstance.defineTransition(STATE_B, STATE_C, EVENT_BC);
            theInstance.defineTransition(STATE_B, STATE_B, EVENT_BB);
            theInstance.defineTransition(STATE_B, STATE_A, EVENT_BA);
            
            theInstance.setStartState(STATE_A);
            
            System.err.println(theInstance);
        }
        
        return theInstance;
    }
    
    @Test(expected=TransitionNotDefinedException.class)
    public void testSuccessfulTransitions() throws StateMachineException {
        StateMachine sm = createStateMachine();
            
        sm.setController(new DumbController(true));
        sm.processEvent(EVENT_AB, null);
        sm.processEvent(EVENT_BB, null);
        sm.processEvent(EVENT_BC, null);
            
        sm.processEvent(EVENT_AB, null);
    }
}
