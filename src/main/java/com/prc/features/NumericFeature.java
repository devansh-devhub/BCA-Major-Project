package com.prc.features;

import com.prc.model.Alert;
import java.util.ArrayList;
import java.util.List;

/**
 * Numeric feature for continuous values like severity.
 * Uses "raw" interpretation - treats the value as a continuous scale.
 */
public class NumericFeature extends Feature {
    private double minValue;
    private double maxValue;

    public NumericFeature(String name, String parameter) {
        super(name, parameter, 1); // Numeric features output 1 value
        this.minValue = 0.0;
        this.maxValue = 5.0; // Default for severity
    }

    public NumericFeature(String name, String parameter, double minValue, double maxValue) {
        super(name, parameter, 1);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public double[] extract(Alert alert) {
        Object value = getParameterValue(alert);
        if (value == null || !(value instanceof Number)) {
            return new double[]{0.0};
        }

        double numValue = ((Number) value).doubleValue();
        
        // Normalize to [0, 1] range
        double normalized = (numValue - minValue) / (maxValue - minValue);
        normalized = Math.max(0.0, Math.min(1.0, normalized)); // Clamp to [0, 1]
        
        return new double[]{normalized};
    }

    @Override
    public List<String> getFeatureNames() {
        List<String> names = new ArrayList<>();
        names.add(name);
        return names;
    }
}
