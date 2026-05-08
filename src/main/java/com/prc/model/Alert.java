package com.prc.model;

import java.util.Map;

/**
 * Represents an alert in Moogsoft format.
 * This is a simplified version matching the core fields used in PRC.
 */
public class Alert {
    private int alertId;
    private String description;
    private int severity; // 0-5 scale
    private String source; // host
    private String type;
    private String agent;
    private String manager;
    private long firstEventTime;
    private Map<String, Object> customInfo;

    public Alert() {
    }

    public Alert(int alertId, String description, int severity, String source, 
                 String type, String agent, String manager, long firstEventTime) {
        this.alertId = alertId;
        this.description = description;
        this.severity = severity;
        this.source = source;
        this.type = type;
        this.agent = agent;
        this.manager = manager;
        this.firstEventTime = firstEventTime;
    }

    // Getters and Setters
    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public long getFirstEventTime() {
        return firstEventTime;
    }

    public void setFirstEventTime(long firstEventTime) {
        this.firstEventTime = firstEventTime;
    }

    public Map<String, Object> getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(Map<String, Object> customInfo) {
        this.customInfo = customInfo;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "alertId=" + alertId +
                ", description='" + description + '\'' +
                ", severity=" + severity +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", agent='" + agent + '\'' +
                ", manager='" + manager + '\'' +
                '}';
    }
}
