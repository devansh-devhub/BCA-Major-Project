package com.prc.neuralnet;

/**
 * Feature normalizer using z-score normalization.
 * Normalizes features to have mean=0 and std=1.
 * Formula: (x - mean) / std
 */
public class Normaliser {
    private double[] means;
    private double[] stds;
    private int sampleCount;
    private boolean fitted;

    public Normaliser(int featureSize) {
        this.means = new double[featureSize];
        this.stds = new double[featureSize];
        this.sampleCount = 0;
        this.fitted = false;
        
        // Initialize stds to 1.0 to avoid division by zero
        for (int i = 0; i < featureSize; i++) {
            stds[i] = 1.0;
        }
    }

    /**
     * Fit the normalizer on training data (calculate mean and std).
     */
    public void fit(double[][] features) {
        int featureSize = features[0].length;
        sampleCount = features.length;

        // Calculate sum and sum of squares for each feature
        double[] sums = new double[featureSize];
        double[] sumSquares = new double[featureSize];

        for (double[] sample : features) {
            for (int i = 0; i < featureSize; i++) {
                sums[i] += sample[i];
                sumSquares[i] += sample[i] * sample[i];
            }
        }

        // Calculate mean
        for (int i = 0; i < featureSize; i++) {
            means[i] = sums[i] / sampleCount;
        }

        // Calculate std
        for (int i = 0; i < featureSize; i++) {
            double variance = (sumSquares[i] / sampleCount) - (means[i] * means[i]);
            stds[i] = Math.sqrt(Math.max(variance, 1e-8)); // Avoid division by zero
        }

        fitted = true;
    }

    /**
     * Normalize features using fitted mean and std.
     */
    public double[] normalize(double[] features) {
        if (!fitted) {
            return features.clone(); // Return as-is if not fitted
        }

        double[] normalized = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            normalized[i] = (features[i] - means[i]) / stds[i];
        }
        return normalized;
    }

    /**
     * Normalize a 2D array of features.
     */
    public double[][] normalize(double[][] features) {
        double[][] normalized = new double[features.length][features[0].length];
        for (int i = 0; i < features.length; i++) {
            normalized[i] = normalize(features[i]);
        }
        return normalized;
    }

    public double[] getMeans() {
        return means.clone();
    }

    public double[] getStds() {
        return stds.clone();
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public boolean isFitted() {
        return fitted;
    }
}
