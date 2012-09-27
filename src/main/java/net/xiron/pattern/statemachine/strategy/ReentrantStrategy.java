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
package net.xiron.pattern.statemachine.strategy;

import net.xiron.pattern.statemachine.PhaseEnterResult;
import net.xiron.pattern.statemachine.StateMachine;
import net.xiron.pattern.statemachine.StateMachineDefinition;
import net.xiron.pattern.statemachine.StateMachineStrategy;
import net.xiron.pattern.statemachine.TransitionController;
import net.xiron.pattern.statemachine.TransitionEvent;
import net.xiron.pattern.statemachine.TransitionLifecycleController;
import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single-thread implementation that doesn't allow reentrant transitions
 */
public class ReentrantStrategy implements StateMachineStrategy {
    private static Logger l = LoggerFactory.getLogger(ReentrantStrategy.class);
    
    private boolean allowsReentrantTransitions;
    private boolean inTransition = false;
    
    public ReentrantStrategy() {
        this(false);
    }
    
    protected ReentrantStrategy(boolean allowsReentrant) {
        this.allowsReentrantTransitions = allowsReentrant;
    }
    
    @Override
    public void processEvent(StateMachine statemachine,
                             String event, Object object,
                             TransitionController controller,
                             TransitionLifecycleController lifecycle)
            throws ReentrantTransitionNotAllowed, EventNotDefinedException,
            TransitionNotDefinedException 
    {
        StateMachineDefinition stateMachineDefinition = statemachine.getStateMachineDefinition();
        if (!stateMachineDefinition.isEvent(event))
            throw new EventNotDefinedException("Event " + event + " not defined");
        
        if (!allowsReentrantTransitions) {
            if (inTransition) {
                throw new ReentrantTransitionNotAllowed("Reentrance from the same thread is not allowed");
            } else {
                inTransition = true;
            }    
        } else {
            inTransition = true;
        }
        
        
        try {
            String source = statemachine.getCurrentState();
            String target = stateMachineDefinition.getTargetState(source, event);
            TransitionEvent tEvent = new TransitionEvent(source, event, target, object);
            
            if (controller.exitStatePhase(tEvent)) {
                controller.transitionPhase(tEvent);
                statemachine.setCurrentState(target);
                PhaseEnterResult result = controller.enterStatePhase(tEvent);
                if (result != null) {
                    l.debug("#processEvent: Redirecting forced by controller to event " + result.getEvent());
                    inTransition = false; 
                    
                    this.processEvent(statemachine, 
                                      result.getEvent(), 
                                      result.getObject(), 
                                      controller,
                                      lifecycle);
                }
            } else {
                if (l.isDebugEnabled())
                    l.debug("#processEvent: transition cancelled on exit state phase");
            }
        } finally {
            inTransition = false;
        }
    }
}
