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

import java.util.ArrayList;
import java.util.List;

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

public class ReentrantEnqueueStrategy implements StateMachineStrategy {
    private static Logger l = LoggerFactory.getLogger(ReentrantEnqueueStrategy.class);
    
    private NonReentrantStrategy proxiedStrategy;
    private boolean inTransition = false;
    private List<TransitionEvent> transitionQueue;
    
    public ReentrantEnqueueStrategy() {
        proxiedStrategy = new NonReentrantStrategy();
        transitionQueue = new ArrayList<TransitionEvent> ();
    }
    
    @Override
    public void processEvent(StateMachine statemachine, String event,
                             Object object, TransitionController controller,
                             TransitionLifecycleController lifecycle)
            throws ReentrantTransitionNotAllowed, EventNotDefinedException,
            TransitionNotDefinedException 
    {
        StateMachineDefinition definition = statemachine.getStateMachineDefinition();
        if (!definition.isEvent(event))
            throw new EventNotDefinedException("Event " + event + " not defined");
        
        this.transitionQueue.add(new TransitionEvent(null, event, null, object));
        
        boolean goOn = false;
        synchronized (transitionQueue) {
            if (!inTransition) {
                inTransition = true;
                goOn = true;
            }
        }
        
        if (goOn) {
            try {
                do {
                    TransitionEvent te = this.transitionQueue.remove(0);
                    this.proxiedStrategy.processEvent(statemachine, te.getEvent(), te.getObject(), controller, lifecycle);
                } while (!this.transitionQueue.isEmpty());
            } catch (ReentrantTransitionNotAllowed t) {
             // TODO. throw t;
            } catch (EventNotDefinedException t) {
             // TODO. throw t;
            } catch (TransitionNotDefinedException t) {
             // TODO. throw t;
            } finally {
                inTransition = false;
            }
        } else {
            l.debug("#processEvent: enqueuing request. size is " + transitionQueue.size());
        }
    }
}
