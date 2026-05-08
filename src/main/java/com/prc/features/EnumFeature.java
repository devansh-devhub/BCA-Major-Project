package com.prc.features;

import com.prc.model.Alert;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Enumerated feature for categorical values like agent, manager.
 * Each unique value maps to a unique position in the feature vector.
 * Uses one-hot encoding.
 */
public class EnumFeature extends Feature {
    private TreeMap<String, Integer> valueToIndex;
    private boolean caseInsensitive;
    private int maxSize; // Maximum number of unique values to track

    public EnumFeature(String name, String parameter, int maxSize, boolean caseInsensitive) {
        super(name, parameter, maxSize);
        this.valueToIndex = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.caseInsensitive = caseInsensitive;
        this.maxSize = maxSize;
    }

    /**
     * Add a value to the enumeration mapping.
     */
    public void addValue(String value) {
        if (valueToIndex.size() >= maxSize) {
            return; // Don't add if we've reached max size
        }

        String key = caseInsensitive ? value.toLowerCase() : value;
        if (!valueToIndex.containsKey(key)) {
            valueToIndex.put(key, valueToIndex.size());
        }
    }

    /**
     * Initialize with a set of known values.
     */
    public void initializeValues(List<String> values) {
        valueToIndex.clear();
        for (String value : values) {
            if (valueToIndex.size() >= maxSize) {
                break;
            }
            String key = caseInsensitive ? value.toLowerCase() : value;
            if (!valueToIndex.containsKey(key)) {
                valueToIndex.put(key, valueToIndex.size());
            }
        }
        this.featureSize = valueToIndex.size();
    }

    @Override
    public double[] extract(Alert alert) {
        Object value = getParameterValue(alert);
        if (value == null) {
            return new double[featureSize]; // All zeros
        }

        String strValue = value.toString();
        String key = caseInsensitive ? strValue.toLowerCase() : strValue;
        
        Integer index = valueToIndex.get(key);
        if (index == null) {
            // Unknown value - could add it dynamically or return zeros
            // For simplicity, return zeros
            return new double[featureSize];
        }

        // One-hot encoding
        double[] features = new double[featureSize];
        features[index] = 1.0;
        
        return features;
    }

    @Override
    public List<String> getFeatureNames() {
        List<String> names = new ArrayList<>();
        for (String value : valueToIndex.keySet()) {
            names.add(name + "_" + value);
        }
        return names;
    }

    public int getCurrentSize() {
        return valueToIndex.size();
    }

    public List<String> getKnownValues() {
        return new ArrayList<>(valueToIndex.keySet());
    }
}
