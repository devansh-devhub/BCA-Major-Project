package com.prc.training;

/**
 * Represents a single training example.
 * Contains feature vector and the expected label (0.0 or 1.0).
 */
public class TrainingData {
    private double[] features;
    private double label; // 1.0 for root cause, 0.0 for non-causal
    private int alertId;
    private int sigId;

    public TrainingData(double[] features, double label, int alertId, int sigId) {
        this.features = features;
        this.label = label;
        this.alertId = alertId;
        this.sigId = sigId;
    }

    public double[] getFeatures() {
        return features;
    }

    public double getLabel() {
        return label;
    }

    public int getAlertId() {
        return alertId;
    }

    public int getSigId() {
        return sigId;
    }
}
