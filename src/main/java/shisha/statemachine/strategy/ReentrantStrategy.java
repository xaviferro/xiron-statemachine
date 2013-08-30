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
package shisha.statemachine.strategy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shisha.statemachine.EnterStateController;
import shisha.statemachine.EventInfo;
import shisha.statemachine.ExitStateController;
import shisha.statemachine.StateMachineDefinitionImpl;
import shisha.statemachine.StateMachineImpl;
import shisha.statemachine.StateMachineStrategy;
import shisha.statemachine.TransitionController;
import shisha.statemachine.TransitionInfo;
import shisha.statemachine.exceptions.EventNotDefinedException;
import shisha.statemachine.exceptions.ReentrantTransitionNotAllowed;
import shisha.statemachine.exceptions.StateMachineDefinitionException;

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
    
    public void processEvent(StateMachineImpl statemachine,
                             String event, Object object)
            throws ReentrantTransitionNotAllowed, StateMachineDefinitionException
    {
        StateMachineDefinitionImpl stateMachineDefinition = (StateMachineDefinitionImpl) statemachine.getDefinition();
        if (!stateMachineDefinition.isEvent(event))
            throw new EventNotDefinedException("Event " + event + " not defined");
        
        try {
            // More fair approach when locking resources than
            // the normal tryLock one
            lock.tryLock(0, TimeUnit.SECONDS); 
            
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
            
            ExitStateController exitController = stateMachineDefinition.getExitStateController(source);
            EnterStateController enterController = stateMachineDefinition.getEnterStateController(target);
            TransitionController transitionController = stateMachineDefinition.getTransitionController(source, event);
            
            if (exitController != null) {
                if (!exitController.execute(tEvent)) {
                    l.debug("The controller cancelled the event propagation");
                    return;
                }
            } 
            
            if (transitionController != null) {
                transitionController.execute(tEvent);
            }
            statemachine.setCurrentState(target);
            EventInfo result = null;
            if (enterController != null) {
                result = enterController.execute(tEvent);
            }
            
            if (result != null) {
                l.debug("#processEvent: Redirecting forced by controller to event " + result.getEvent());
                inTransition = false; 
                
                this.processEvent(statemachine, 
                                  result.getEvent(), 
                                  result.getObject());
            }
        } catch (InterruptedException ie) {
            l.warn("#processEvent: interrupted exception might not happen");
        } finally {
            inTransition = false;
            lock.unlock();
        }
    }
}
