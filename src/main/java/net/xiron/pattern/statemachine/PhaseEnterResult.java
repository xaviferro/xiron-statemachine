package net.xiron.pattern.statemachine;

public class PhaseEnterResult {
    private String event;
    private Object object;
    
    public PhaseEnterResult(String event, Object object) {
        this.event = event;
        this.object = object;
    }
    
    public String getEvent() {
        return this.event;
    }
    
    public Object getObject() {
        return this.object;
    }
}
