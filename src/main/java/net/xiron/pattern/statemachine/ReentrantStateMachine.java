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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.TransitionNotDefinedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReentrantStateMachine implements StateMachine {
    private static Logger l = LoggerFactory.getLogger(ReentrantStateMachine.class);
    
    /**
     * We will delegate all the operations to the state machine but 
     * the process event itself
     */
    private StateMachineImpl proxiedStateMachine;
    private boolean inTransition = false;
    private List<TransitionEvent> transitionQueue;
    
    public ReentrantStateMachine() {
        this.proxiedStateMachine = new StateMachineImpl();
        this.transitionQueue = Collections.synchronizedList(new ArrayList<TransitionEvent> ());
    }
    
    @Override public boolean allowsReentrantTransitions() {
        return true;
    }

    @Override public void defineState(String state) {
        this.proxiedStateMachine.defineState(state);
    }

    @Override public void setStartState(String state) throws StateNotDefinedException {
        this.proxiedStateMachine.setStartState(state);
    }

    @Override public void defineEvent(String event) {
        this.proxiedStateMachine.defineEvent(event);
    }

    @Override public void defineTransition(String sourceState, String targetState, String event) 
        throws StateNotDefinedException, EventNotDefinedException 
    {
        this.proxiedStateMachine.defineTransition(sourceState, targetState, event);
    }

    @Override public void processEvent(String event, Object object)
        throws ReentrantTransitionNotAllowed, EventNotDefinedException, TransitionNotDefinedException 
    {
        if (!this.proxiedStateMachine.isEvent(event))
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
                    this.proxiedStateMachine.processEvent(te.getEvent(), te.getObject());
                } while (!this.transitionQueue.isEmpty());
            } catch (ReentrantTransitionNotAllowed t) {
                throw t;
            } catch (EventNotDefinedException t) {
                throw t;
            } catch (TransitionNotDefinedException t) {
                throw t;
            } finally {
                inTransition = false;
            }
        } else {
            l.debug("#processEvent: enqueuing request. size is " + transitionQueue.size());
        }
    }
    
    @Override public String getCurrentState() {
        return this.proxiedStateMachine.getCurrentState();
    }

    @Override public void setController(StateMachineController controller) {
        this.proxiedStateMachine.setController(controller);
    }
}
