package com.prc.features;

import com.prc.model.Alert;
import java.util.ArrayList;
import java.util.List;

/**
 * Text feature that tokenizes text and uses a simple hash trick.
 * Simplified version of Moogsoft's CRawTextFeature.
 */
public class TextFeature extends Feature {
    private int featureSize; // Hash space size
    private boolean useWords; // true = word tokenization, false = shingles (not implemented in simple version)

    public TextFeature(String name, String parameter, int featureSize, boolean useWords) {
        super(name, parameter, featureSize);
        this.featureSize = featureSize;
        this.useWords = useWords;
    }

    @Override
    public double[] extract(Alert alert) {
        Object value = getParameterValue(alert);
        if (value == null || !(value instanceof String)) {
            return new double[featureSize]; // Return zeros if no value
        }

        String text = ((String) value).toLowerCase();
        String[] tokens = tokenize(text);
        
        return hashTrick(tokens);
    }

    /**
     * Tokenize text into words.
     */
    private String[] tokenize(String text) {
        if (useWords) {
            // Simple word tokenization by whitespace
            return text.split("\\s+");
        } else {
            // Shingles not implemented in simple version - fall back to words
            return text.split("\\s+");
        }
    }

    /**
     * Simple hash trick: map tokens to a fixed-size feature vector.
     * Each token increments the count at its hash position.
     */
    private double[] hashTrick(String[] tokens) {
        double[] features = new double[featureSize];
        
        for (String token : tokens) {
            int hash = Math.abs(token.hashCode()) % featureSize;
            features[hash] += 1.0; // Increment count
        }
        
        return features;
    }

    @Override
    public List<String> getFeatureNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < featureSize; i++) {
            names.add(name + "_hash_" + i);
        }
        return names;
    }
}
