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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import net.xiron.pattern.statemachine.EventInfo;
import net.xiron.pattern.statemachine.StateMachine;
import net.xiron.pattern.statemachine.StateMachineDefinition;
import net.xiron.pattern.statemachine.StateMachineStrategy;
import net.xiron.pattern.statemachine.TransitionController;
import net.xiron.pattern.statemachine.TransitionInfo;
import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateMachineDefinitionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single-thread implementation which user can configure whether it allows reentrant 
 * transitions
 */
public class ReentrantStrategy implements StateMachineStrategy {
    private static Logger l = LoggerFactory.getLogger(ReentrantStrategy.class);
    
    private ReentrantLock lock = new ReentrantLock();
    private boolean allowsReentrantTransitions;
    private boolean inTransition = false;
    
    /**
     * By default, we don't allow reentrant transitions. That means that if there
     * is a running transition and the developer, by mistake, tries to push
     * another transition from the same thread out of the allowed flow, it will
     * throw an exception
     */
    public ReentrantStrategy() {
        this(false);
    }
    
    protected ReentrantStrategy(boolean allowsReentrant) {
        this.allowsReentrantTransitions = allowsReentrant;
    }
    
    @Override
    public void processEvent(StateMachine statemachine,
                             String event, Object object,
                             TransitionController controller)
            throws ReentrantTransitionNotAllowed, StateMachineDefinitionException
    {
        StateMachineDefinition stateMachineDefinition = statemachine.getDefinition();
        if (!stateMachineDefinition.isEvent(event))
            throw new EventNotDefinedException("Event " + event + " not defined");
        
        try {
            lock.tryLock(0, TimeUnit.SECONDS); // Fair approach when locking resources
            
            if (!allowsReentrantTransitions) {
                if (inTransition) {
                    throw new ReentrantTransitionNotAllowed("Reentrance from the same thread is not allowed");
                } else {
                    inTransition = true;
                }    
            } 
        
            String source = statemachine.getCurrentState();
            String target = stateMachineDefinition.getTargetState(source, event);
            TransitionInfo tEvent = new TransitionInfo(source, event, target, object);
            
            if (controller.exitStatePhase(tEvent)) {
                controller.transitionPhase(tEvent);
                statemachine.setCurrentState(target);
                EventInfo result = controller.enterStatePhase(tEvent);
                if (result != null) {
                    l.debug("#processEvent: Redirecting forced by controller to event " + result.getEvent());
                    inTransition = false; 
                    
                    this.processEvent(statemachine, 
                                      result.getEvent(), 
                                      result.getObject(), 
                                      controller);
                }
            } else {
                if (l.isDebugEnabled())
                    l.debug("#processEvent: transition cancelled on exit state phase");
            }
        } catch (InterruptedException ie) {
            l.warn("#processEvent: interrupted exception might not happen");
        } finally {
            inTransition = false;
            lock.unlock();
        }
    }
}
