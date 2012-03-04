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
