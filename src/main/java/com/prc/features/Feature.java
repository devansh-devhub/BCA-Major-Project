package com.prc.features;

import com.prc.model.Alert;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all feature types.
 * A feature extracts a specific piece of information from an alert.
 */
public abstract class Feature {
    protected String name;
    protected String parameter; // The alert field to extract
    protected int featureSize;

    public Feature(String name, String parameter, int featureSize) {
        this.name = name;
        this.parameter = parameter;
        this.featureSize = featureSize;
    }

    /**
     * Extract feature values from an alert.
     * 
     * @param alert The alert to extract from
     * @return Feature vector as double array
     */
    public abstract double[] extract(Alert alert);

    /**
     * Get the size of this feature's output vector.
     */
    public int getFeatureSize() {
        return featureSize;
    }

    /**
     * Get the feature name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the parameter this feature extracts.
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Get feature names for each dimension (for debugging).
     * Default implementation returns generic names.
     */
    public List<String> getFeatureNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < featureSize; i++) {
            names.add(name + "_" + i);
        }
        return names;
    }

    /**
     * Helper method to get a parameter value from an alert by reflection.
     * This is a simplified version - in production, you'd use a more robust approach.
     */
    protected Object getParameterValue(Alert alert) {
        switch (parameter.toLowerCase()) {
            case "description":
                return alert.getDescription();
            case "severity":
                return alert.getSeverity();
            case "source":
                return alert.getSource();
            case "type":
                return alert.getType();
            case "agent":
                return alert.getAgent();
            case "manager":
                return alert.getManager();
            case "first_event_time":
                return alert.getFirstEventTime();
            default:
                return null;
        }
    }
}
