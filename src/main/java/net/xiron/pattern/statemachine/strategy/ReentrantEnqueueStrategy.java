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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.xiron.pattern.statemachine.StateMachine;
import net.xiron.pattern.statemachine.StateMachineDefinition;
import net.xiron.pattern.statemachine.StateMachineStrategy;
import net.xiron.pattern.statemachine.TransitionController;
import net.xiron.pattern.statemachine.exceptions.EventNotDefinedException;
import net.xiron.pattern.statemachine.exceptions.ReentrantTransitionNotAllowed;
import net.xiron.pattern.statemachine.exceptions.StateMachineDefinitionException;
import net.xiron.pattern.statemachine.exceptions.StateMachineException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy contains a different thread for processing the transitions.
 * When a final state is reached, this thread will be destroyed.
 */
public class ReentrantEnqueueStrategy implements StateMachineStrategy {
    private static Logger l = LoggerFactory
            .getLogger(ReentrantEnqueueStrategy.class);

    private NonReentrantStrategy proxiedStrategy;
    //private List<ProcessEvent> transitionQueue;
    private BlockingQueue<ProcessEvent> pendingEvents;
    private Thread worker;
    private StrategyWorker strategyWorker;

    public ReentrantEnqueueStrategy() {
        proxiedStrategy = new NonReentrantStrategy();
        //transitionQueue = new ArrayList<ProcessEvent>();
        pendingEvents = new LinkedBlockingQueue<ProcessEvent>();

        strategyWorker = new StrategyWorker(pendingEvents);
        worker = new Thread(strategyWorker);
        worker.start();
    }

    @Override
    public void processEvent(StateMachine statemachine, String event,
                             Object object, TransitionController controller)
            throws ReentrantTransitionNotAllowed,
            StateMachineDefinitionException {
        StateMachineDefinition definition = statemachine
                .getDefinition();
        if (!definition.isEvent(event))
            throw new EventNotDefinedException("Event " + event
                    + " not defined");

        try {
            l.debug("#processEvent: Adding event to the queue list ", event);
            this.pendingEvents.put(new ProcessEvent(statemachine, event, object,
                    controller));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Leverage all resources that this strategy might have acquired
     */
    public void cleanUp() {
        worker.interrupt();
    }

    class ProcessEvent {
        public ProcessEvent(StateMachine sm, String event, Object object,
                TransitionController controller) {
            super();
            this.sm = sm;
            this.event = event;
            this.object = object;
            this.controller = controller;
        }

        public StateMachine getStateMachine() {
            return sm;
        }

        public String getEvent() {
            return event;
        }

        public Object getObject() {
            return object;
        }

        public TransitionController getController() {
            return controller;
        }

        String event;
        StateMachine sm;
        Object object;
        TransitionController controller;
    }

    class StrategyWorker implements Runnable {
        private final BlockingQueue<ProcessEvent> queue;

        StrategyWorker(BlockingQueue<ProcessEvent> q) {
            queue = q;
        }

        public void run() {
            try {
                while (true) {
                    try {
                        ProcessEvent event = queue.take();
                        proxiedStrategy.processEvent(event.getStateMachine(),
                                event.getEvent(), event.getObject(),
                                event.getController());
                    } catch (StateMachineException sme) {
                        l.warn("StateMachineEXception", sme);
                    }
                }
            } catch (InterruptedException ex) {
                l.debug("Interrupted, so cleaning up the queue");
            }
        }
    }
}
