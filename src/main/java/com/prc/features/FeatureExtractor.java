package com.prc.features;

import com.prc.model.Alert;
import java.util.*;

/**
 * Main coordinator for feature extraction.
 * Manages active features and extracts feature vectors from alerts.
 */
public class FeatureExtractor {
    private List<Feature> activeFeatures;
    private int totalFeatureSize;

    public FeatureExtractor() {
        this.activeFeatures = new ArrayList<>();
        this.totalFeatureSize = 0;
    }

    /**
     * Add a feature to the active feature list.
     */
    public void addFeature(Feature feature) {
        activeFeatures.add(feature);
        totalFeatureSize += feature.getFeatureSize();
    }

    /**
     * Extract features from an alert and return as a feature vector.
     * 
     * @param alert The alert to extract features from
     * @return Feature vector as double array
     */
    public double[] extractFeatures(Alert alert) {
        double[] featureVector = new double[totalFeatureSize];
        int offset = 0;

        for (Feature feature : activeFeatures) {
            double[] featureValues = feature.extract(alert);
            System.arraycopy(featureValues, 0, featureVector, offset, featureValues.length);
            offset += featureValues.length;
        }

        return featureVector;
    }

    /**
     * Extract features from multiple alerts (for a situation).
     * 
     * @param alerts List of alerts
     * @return 2D array where each row is a feature vector for an alert
     */
    public double[][] extractFeatures(List<Alert> alerts) {
        double[][] featureMatrix = new double[alerts.size()][totalFeatureSize];
        
        for (int i = 0; i < alerts.size(); i++) {
            featureMatrix[i] = extractFeatures(alerts.get(i));
        }
        
        return featureMatrix;
    }

    public int getTotalFeatureSize() {
        return totalFeatureSize;
    }

    public List<Feature> getActiveFeatures() {
        return new ArrayList<>(activeFeatures);
    }

    /**
     * Get feature names for debugging/interpretation.
     */
    public List<String> getFeatureNames() {
        List<String> names = new ArrayList<>();
        for (Feature feature : activeFeatures) {
            names.addAll(feature.getFeatureNames());
        }
        return names;
    }
}
