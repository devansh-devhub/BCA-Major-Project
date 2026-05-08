package com.prc.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Manages PRC configuration from JSON file.
 * Handles feature selection, neural network parameters, and training strategy.
 */
public class ConfigManager {
    private String configPath;
    private Map<String, Object> config;
    
    // Default configuration
    private static final Map<String, Object> DEFAULT_CONFIG = new HashMap<>();
    static {
        DEFAULT_CONFIG.put("active_features", Arrays.asList("Description", "Severity", "Host"));
        DEFAULT_CONFIG.put("hidden_layer_size", 10);
        DEFAULT_CONFIG.put("learning_rate", 0.01);
        DEFAULT_CONFIG.put("max_epochs", 50);
        DEFAULT_CONFIG.put("training_strategy", "incremental");
        DEFAULT_CONFIG.put("feature_sizes", new HashMap<String, Integer>() {{
            put("Description", 256);
            put("Host", 32);
        }});
    }

    public ConfigManager(String configPath) {
        this.configPath = configPath;
        this.config = new HashMap<>(DEFAULT_CONFIG);
    }

    /**
     * Load configuration from JSON file.
     */
    public void loadConfig() throws IOException {
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            System.out.println("Config file not found, using defaults");
            saveConfig(); // Save defaults
            return;
        }

        String content = new String(Files.readAllBytes(Paths.get(configPath)));
        parseConfig(content);
        System.out.println("Configuration loaded from: " + configPath);
    }

    /**
     * Save current configuration to JSON file.
     */
    public void saveConfig() throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        // Active features
        json.append("  \"active_features\": [");
        List<String> activeFeatures = getActiveFeatures();
        for (int i = 0; i < activeFeatures.size(); i++) {
            json.append("\"").append(activeFeatures.get(i)).append("\"");
            if (i < activeFeatures.size() - 1) json.append(", ");
        }
        json.append("],\n");
        
        // Neural network parameters
        json.append("  \"hidden_layer_size\": ").append(getHiddenLayerSize()).append(",\n");
        json.append("  \"learning_rate\": ").append(getLearningRate()).append(",\n");
        json.append("  \"max_epochs\": ").append(getMaxEpochs()).append(",\n");
        
        // Training strategy
        json.append("  \"training_strategy\": \"").append(getTrainingStrategy()).append("\",\n");
        
        // Feature sizes
        json.append("  \"feature_sizes\": {\n");
        Map<String, Integer> featureSizes = getFeatureSizes();
        int count = 0;
        for (Map.Entry<String, Integer> entry : featureSizes.entrySet()) {
            json.append("    \"").append(entry.getKey()).append("\": ").append(entry.getValue());
            if (count < featureSizes.size() - 1) json.append(",");
            json.append("\n");
            count++;
        }
        json.append("  }\n");
        
        json.append("}\n");
        
        Files.write(Paths.get(configPath), json.toString().getBytes());
        System.out.println("Configuration saved to: " + configPath);
    }

    /**
     * Parse configuration from JSON string (simplified parsing).
     */
    private void parseConfig(String content) {
        // Extract active features
        List<String> features = extractStringList(content, "active_features");
        if (!features.isEmpty()) {
            config.put("active_features", features);
        }
        
        // Extract numeric parameters
        int hiddenSize = extractInt(content, "hidden_layer_size");
        if (hiddenSize > 0) config.put("hidden_layer_size", hiddenSize);
        
        double learningRate = extractDouble(content, "learning_rate");
        if (learningRate > 0) config.put("learning_rate", learningRate);
        
        int maxEpochs = extractInt(content, "max_epochs");
        if (maxEpochs > 0) config.put("max_epochs", maxEpochs);
        
        String strategy = extractString(content, "training_strategy");
        if (strategy != null) config.put("training_strategy", strategy);
        
        // Extract feature sizes (simplified)
        Map<String, Integer> featureSizes = new HashMap<>();
        extractFeatureSizes(content, featureSizes);
        if (!featureSizes.isEmpty()) {
            config.put("feature_sizes", featureSizes);
        }
    }

    // Getters
    public List<String> getActiveFeatures() {
        @SuppressWarnings("unchecked")
        List<String> features = (List<String>) config.get("active_features");
        return features != null ? features : (List<String>) DEFAULT_CONFIG.get("active_features");
    }

    public int getHiddenLayerSize() {
        Integer size = (Integer) config.get("hidden_layer_size");
        return size != null ? size : (Integer) DEFAULT_CONFIG.get("hidden_layer_size");
    }

    public double getLearningRate() {
        Double rate = (Double) config.get("learning_rate");
        return rate != null ? rate : (Double) DEFAULT_CONFIG.get("learning_rate");
    }

    public int getMaxEpochs() {
        Integer epochs = (Integer) config.get("max_epochs");
        return epochs != null ? epochs : (Integer) DEFAULT_CONFIG.get("max_epochs");
    }

    public String getTrainingStrategy() {
        String strategy = (String) config.get("training_strategy");
        return strategy != null ? strategy : (String) DEFAULT_CONFIG.get("training_strategy");
    }

    @SuppressWarnings("unchecked")
    public Map<String, Integer> getFeatureSizes() {
        Map<String, Integer> sizes = (Map<String, Integer>) config.get("feature_sizes");
        return sizes != null ? sizes : (Map<String, Integer>) DEFAULT_CONFIG.get("feature_sizes");
    }

    // Setters
    public void setActiveFeatures(List<String> features) {
        config.put("active_features", features);
    }

    public void setHiddenLayerSize(int size) {
        config.put("hidden_layer_size", size);
    }

    public void setLearningRate(double rate) {
        config.put("learning_rate", rate);
    }

    public void setMaxEpochs(int epochs) {
        config.put("max_epochs", epochs);
    }

    public void setTrainingStrategy(String strategy) {
        config.put("training_strategy", strategy);
    }

    public void setFeatureSizes(Map<String, Integer> sizes) {
        config.put("feature_sizes", sizes);
    }

    /**
     * Display current configuration.
     */
    public void displayConfig() {
        System.out.println("\n=== Current PRC Configuration ===");
        System.out.println("Active Features: " + getActiveFeatures());
        System.out.println("Hidden Layer Size: " + getHiddenLayerSize());
        System.out.println("Learning Rate: " + getLearningRate());
        System.out.println("Max Epochs: " + getMaxEpochs());
        System.out.println("Training Strategy: " + getTrainingStrategy());
        System.out.println("Feature Sizes: " + getFeatureSizes());
        System.out.println("===================================\n");
    }

    // Simplified JSON extraction methods
    private List<String> extractStringList(String content, String key) {
        List<String> result = new ArrayList<>();
        int start = content.indexOf("\"" + key + "\"");
        if (start == -1) return result;
        
        start = content.indexOf("[", start) + 1;
        int end = content.indexOf("]", start);
        
        String arrayContent = content.substring(start, end);
        String[] items = arrayContent.split(",");
        for (String item : items) {
            String value = item.trim().replaceAll("\"", "");
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result;
    }

    private int extractInt(String content, String key) {
        int start = content.indexOf("\"" + key + "\"");
        if (start == -1) return 0;
        
        start = content.indexOf(":", start) + 1;
        int end = content.indexOf(",", start);
        if (end == -1) end = content.indexOf("\n", start);
        
        try {
            return Integer.parseInt(content.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double extractDouble(String content, String key) {
        int start = content.indexOf("\"" + key + "\"");
        if (start == -1) return 0.0;
        
        start = content.indexOf(":", start) + 1;
        int end = content.indexOf(",", start);
        if (end == -1) end = content.indexOf("\n", start);
        
        try {
            return Double.parseDouble(content.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String extractString(String content, String key) {
        int start = content.indexOf("\"" + key + "\"");
        if (start == -1) return null;
        
        start = content.indexOf(":", start) + 1;
        int end = content.indexOf(",", start);
        if (end == -1) end = content.indexOf("\n", start);
        
        return content.substring(start, end).trim().replaceAll("\"", "");
    }

    private void extractFeatureSizes(String content, Map<String, Integer> featureSizes) {
        int start = content.indexOf("\"feature_sizes\"");
        if (start == -1) return;
        
        start = content.indexOf("{", start) + 1;
        int end = content.indexOf("}", start);
        
        String objectContent = content.substring(start, end);
        String[] pairs = objectContent.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replaceAll("\"", "");
                try {
                    int value = Integer.parseInt(keyValue[1].trim());
                    featureSizes.put(key, value);
                } catch (NumberFormatException e) {
                    // Skip invalid values
                }
            }
        }
    }
}
