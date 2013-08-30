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

/**
 * <p>Con<tains all information required for processing an event in a state machine. An event
 * is defined by an event identifier and an object -it might be null- that will be useful
 * when processing the transition (some kind of state).
 * 
 * <p>A non null object is returned during the {@link TransitionController#enterStatePhase(TransitionEvent event)}
 * if we want the state machine to process a new event before releasing the lock.
 * 
 * <p>This  is quite useful for some conditional states that might evaluate in runtime next event to be
 * consumed.
 */
public class EventInfo {
    private String event;
    private Object object;
    
    public EventInfo(String event, Object object) {
        this.event = event;
        this.object = object;
    }
    
    /**
     * The event we want to consume after the phase is finished
     */
    public String getEvent() {
        return this.event;
    }
    
    /**
     * The object that we passed to the previous transition
     * @return
     */
    public Object getObject() {
        return this.object;
    }
}
