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

import java.util.HashMap;

public class TransitionEvent {
    private String source;
    private String target;
    private String event;
    private Object theObject;
    // We offer a generic repository for all the distinct phases of a transition
    private HashMap<String, Object> transitionContext;
    
    public TransitionEvent(String source, String event, String target, Object object) {
        this.source = source;
        this.event = event;
        this.target = target;
        this.theObject = object;
        this.transitionContext = new HashMap<String, Object> ();
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
    
    public Object getObject() {
        return theObject;
    }
    
    public HashMap<String,Object> getTransitionContext() {
        return this.transitionContext;
    }
    
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("source: ").append(source).append(" - event: ").append(event).append(" - target: ").append(target);
        return sb.toString();
    }
}
