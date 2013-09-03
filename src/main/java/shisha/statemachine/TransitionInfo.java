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
package shisha.statemachine;

import java.util.HashMap;

import com.google.common.collect.Maps;

/**
 * Contains the transition's information. Besides the basic information (source,
 * target and event), we provide the object passed when processing the event and
 * a transition context map which is really helpful when we need to store information
 * between phases of the same transition.
 */
public class TransitionInfo extends EventInfo {
    private String source;
    private String target;
    
    // We offer a generic repository for all the distinct phases of a transition
    private HashMap<String, Object> transitionContext;
    
    public TransitionInfo(String source, String event, String target, Object object) {
        super(event, object);
        
        this.source = source;
        this.target = target;
        this.transitionContext = Maps.newHashMap();
    }
    
    public String getSource() {
        return source;
    }
    
    public String getTarget() {
        return target;
    }
    
    public HashMap<String,Object> getTransitionContext() {
        return this.transitionContext;
    }
    
    public String toString() {
        return "[" + source + " + " + event + " -> " + target + "]";
        
    }
}
