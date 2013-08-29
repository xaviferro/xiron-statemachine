/*  
 * Copyright 2012-2013 xavi.ferro
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
package shisha.statemachine;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;

import shisha.statemachine.annotations.Transition;
import shisha.statemachine.exceptions.StateMachineException;

public class TransitionInvoker {
    private static Logger l = getLogger(TransitionInvoker.class);

    private String source;
    private String target;
    private String event;
    private Method method;
    private Object instance;

    public TransitionInvoker(Transition transition, Method method, Object instance) {
        this(transition.source(), transition.event(), transition.target(), method, instance);
    }

    public TransitionInvoker(String source, String event, String target, Method method,
            Object instance) {
        super();
        this.source = source;
        this.target = target;
        this.event = event;
        this.method = method;
        this.instance = instance;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getEvent() {
        return event;
    }

    public Method getMethod() {
        return method;
    }

    public Object getCallee() {
        return instance;
    }

    public boolean executeExitPhase() throws StateMachineException {
        boolean result = true;
        TransitionInfo evt = new TransitionInfo(source, event, target, instance);
        try {
            result = (Boolean) method.invoke(instance, evt);
        } catch (IllegalAccessException e) {
            l.error("#executeExitPhase error. This SHOULD not happen", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof StateMachineException) {
                throw (StateMachineException) e.getCause();
            } else {
                l.error("#executeExitPhase error. This should never happen", e);
            }
        }
        return result;
    }

    /*
     * Exceptions at this point are shouldn't be taken into account as we have
     * already checked them when creating them
     */
    public void executeTransitionPhase() throws StateMachineException {
        try {
            TransitionInfo evt = new TransitionInfo(source, event, target, instance);
            method.invoke(instance, evt);
        } catch (IllegalAccessException e) {
            l.error("#executeTransitionPhase error", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof StateMachineException) {
                throw (StateMachineException) e.getCause();
            } else {
                l.error("#executeTransitionPhase error. This should never happen", e);
            }
        }
    }

    public EventInfo executeEnterPhase() throws StateMachineException {
        EventInfo result = null;
        try {
            TransitionInfo evt = new TransitionInfo(source, event, target, instance);
            result = (EventInfo) method.invoke(instance, evt);
        } catch (IllegalAccessException e) {
            l.error("#executeEnterPhase error. This should never happen", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof StateMachineException) {
                throw (StateMachineException) e.getCause();
            } else {
                l.error("#executeEnterPhase error. This should never happen", e);
            }
        }
        return result;
    }
}
