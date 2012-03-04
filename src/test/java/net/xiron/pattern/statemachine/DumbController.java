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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dumb annotated controller useful for testing. Only logs events.
 * One can configure the return value in the phaseExit in the constructor.
 * It does not redirect on the exit phase.
 * 
 * @author xavi.ferro
 */
public class DumbController implements StateMachineController {
    private boolean phaseExit;
    private Logger l = LoggerFactory.getLogger(DumbController.class);
    
    public DumbController(boolean phaseExit) {
        this.phaseExit = phaseExit;
    }
    
    @Override
    public boolean phaseExitState(TransitionEvent evt) {
        if (l.isDebugEnabled())
            l.debug("#phaseExitState: " + evt.getSource() + " + " + evt.getEvent() + " -> " + evt.getTarget());
        
        return phaseExit;
    }

    @Override
    public void phaseTransition(TransitionEvent evt) {
        if (l.isDebugEnabled())
            l.debug("#phaseTransition: " + evt.getSource() + " + " + evt.getEvent() + " -> " + evt.getTarget());
    }

    @Override
    public PhaseEnterResult phaseEnterState(TransitionEvent evt) {
        if (l.isDebugEnabled())
            l.debug("#phaseEnterState: " + evt.getSource() + " + " + evt.getEvent() + " -> " + evt.getTarget());
        
        return null;
    }
}
