package com.prc.model;

/**
 * Represents user feedback labels for PRC training.
 * Operators label alerts as ROOT_CAUSE, NON_CAUSAL, or UNLABELLED.
 */
public class PrcLabel {
    public enum LabelType {
        ROOT_CAUSE,
        NON_CAUSAL,
        UNLABELLED
    }

    private int alertId;
    private int sigId;
    private LabelType label;
    private String userId; // Who provided the feedback
    private long timestamp;

    public PrcLabel() {
    }

    public PrcLabel(int alertId, int sigId, LabelType label, String userId) {
        this.alertId = alertId;
        this.sigId = sigId;
        this.label = label;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public int getSigId() {
        return sigId;
    }

    public void setSigId(int sigId) {
        this.sigId = sigId;
    }

    public LabelType getLabel() {
        return label;
    }

    public void setLabel(LabelType label) {
        this.label = label;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Convert label to numeric value for training.
     * ROOT_CAUSE = 1.0, NON_CAUSAL = 0.0, UNLABELLED = -1.0 (ignored in training)
     */
    public double toNumericValue() {
        switch (label) {
            case ROOT_CAUSE:
                return 1.0;
            case NON_CAUSAL:
                return 0.0;
            case UNLABELLED:
                return -1.0;
            default:
                return -1.0;
        }
    }

    @Override
    public String toString() {
        return "PrcLabel{" +
                "alertId=" + alertId +
                ", sigId=" + sigId +
                ", label=" + label +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
