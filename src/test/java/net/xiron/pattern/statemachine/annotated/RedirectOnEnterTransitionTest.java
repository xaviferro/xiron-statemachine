package net.xiron.pattern.statemachine.annotated;

import junit.framework.Assert;
import net.xiron.pattern.statemachine.StateMachine;

import org.junit.Test;

public class RedirectOnEnterTransitionTest {
    @Test
    public void testRedirect() throws Exception {
        AnnotatedControllerProcessor processor = new AnnotatedControllerProcessor(new DumbRedirectController());
        processor.processEvent(DumbRedirectController.EVENT_AA, null);
        StateMachine sm = processor.getStateMachine();
        String endState = sm.getCurrentState();
        Assert.assertEquals(endState, DumbRedirectController.STATE_B);
    }
}
