package com.prc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Situation in Moogsoft format.
 * A Situation is a group of related alerts that represent a single incident.
 */
public class Situation {
    private int sigId;
    private String description;
    private List<Alert> alerts;
    private long createdDate;
    private int status; // 9 = closed, other = open

    public Situation() {
        this.alerts = new ArrayList<>();
    }

    public Situation(int sigId, String description, long createdDate) {
        this.sigId = sigId;
        this.description = description;
        this.createdDate = createdDate;
        this.alerts = new ArrayList<>();
        this.status = 1; // Default to open
    }

    // Getters and Setters
    public int getSigId() {
        return sigId;
    }

    public void setSigId(int sigId) {
        this.sigId = sigId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public void addAlert(Alert alert) {
        this.alerts.add(alert);
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAlertCount() {
        return alerts != null ? alerts.size() : 0;
    }

    @Override
    public String toString() {
        return "Situation{" +
                "sigId=" + sigId +
                ", description='" + description + '\'' +
                ", alertCount=" + getAlertCount() +
                ", createdDate=" + createdDate +
                ", status=" + status +
                '}';
    }
}
