package net.xiron.pattern.statemachine.annotated;

import net.xiron.pattern.statemachine.StateMachine;
import net.xiron.pattern.statemachine.TransitionEvent;
import net.xiron.pattern.statemachine.annotated.Event;
import net.xiron.pattern.statemachine.annotated.StartState;
import net.xiron.pattern.statemachine.annotated.State;
import net.xiron.pattern.statemachine.annotated.Transition;
import net.xiron.pattern.statemachine.annotated.Transitions;
import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.IllegalAnnotationException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.IllegalTransitionAnnotationException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatedControllerTest implements AnnotatedController {
    private Logger l = LoggerFactory.getLogger(AnnotatedControllerTest.class);
    private AnnotatedControllerProcessor processor;
    
    @StartState @State public static final String STATE_A = "A";
    @State public static final String STATE_B = "B";
    
    @Event public static final String EVENT_AB = "AB";
    @Event public static final String EVENT_AA = "AA";
    @Event public static final String EVENT_BB = "BB";
    
    public AnnotatedControllerTest() 
        throws StateNotDefinedException, EventNotDefinedException, IllegalAnnotationException 
    {
        processor = new AnnotatedControllerProcessor(this);
    }
    
    @Test
    public void testMe() {
        StateMachine sm = processor.getStateMachine();
        try {
            processor.processEvent(EVENT_AA, null);
        } catch (ReentrantTransitionNotAllowed e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (StateNotDefinedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EventNotDefinedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransitionNotDefinedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        l.info(sm.toString());
    }
    
    @Transitions({@Transition(source=STATE_A,event=EVENT_AA,target=STATE_A),
                  @Transition(source=STATE_A,event=EVENT_AB,target=STATE_B)})
    public void transition(TransitionEvent evt) {
        System.err.println("OEEEE");
    }
    
    @Transition(source=STATE_B,event=EVENT_BB,target=STATE_B)
    public void transition2(TransitionEvent evt) {
        
    }
}
