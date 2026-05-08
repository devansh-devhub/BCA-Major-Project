package com.prc;

import com.prc.features.*;
import com.prc.model.*;
import com.prc.neuralnet.SimpleNeuralNetwork;
import com.prc.persistence.ConfigManager;
import com.prc.persistence.ModelDAO;
import com.prc.training.Trainer;
import com.prc.training.TrainingData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main PRC engine coordinator.
 * Ties together all components: feature extraction, neural network, training, and persistence.
 */
public class PrcEngine {
    private FeatureExtractor featureExtractor;
    private SimpleNeuralNetwork network;
    private ConfigManager configManager;
    private ModelDAO modelDAO;
    private Trainer trainer;
    
    private Map<Integer, Situation> situations;
    private Map<Integer, Alert> alerts;
    private List<PrcLabel> labels;
    
    private static final String DEFAULT_MODEL_NAME = "prc_model";

    public PrcEngine(String configPath, String modelsDirectory) throws IOException {
        this.configManager = new ConfigManager(configPath);
        this.configManager.loadConfig();
        
        this.modelDAO = new ModelDAO(modelsDirectory);
        this.situations = new HashMap<>();
        this.alerts = new HashMap<>();
        this.labels = new ArrayList<>();
        
        initializeFeatureExtractor();
        
        // Try to load existing model
        if (modelDAO.modelExists(DEFAULT_MODEL_NAME)) {
            System.out.println("Loading existing model...");
            network = modelDAO.loadModel(DEFAULT_MODEL_NAME);
            trainer = new Trainer(network, configManager.getLearningRate(), configManager.getMaxEpochs());
        } else {
            System.out.println("No existing model found, will create new one on first training");
            network = null;
            trainer = null;
        }
    }

    /**
     * Initialize feature extractor based on configuration.
     */
    private void initializeFeatureExtractor() {
        featureExtractor = new FeatureExtractor();
        List<String> activeFeatures = configManager.getActiveFeatures();
        Map<String, Integer> featureSizes = configManager.getFeatureSizes();
        
        for (String featureName : activeFeatures) {
            switch (featureName) {
                case "Description":
                    int descSize = featureSizes.getOrDefault("Description", 256);
                    featureExtractor.addFeature(new TextFeature("Description", "description", descSize, true));
                    break;
                case "Host":
                    int hostSize = featureSizes.getOrDefault("Host", 32);
                    featureExtractor.addFeature(new TextFeature("Host", "source", hostSize, true));
                    break;
                case "Severity":
                    featureExtractor.addFeature(new NumericFeature("Severity", "severity", 0, 5));
                    break;
                case "Agent":
                    featureExtractor.addFeature(new EnumFeature("Agent", "agent", 10, true));
                    break;
                case "Manager":
                    featureExtractor.addFeature(new EnumFeature("Manager", "manager", 10, true));
                    break;
                case "Type":
                    int typeSize = featureSizes.getOrDefault("Type", 32);
                    featureExtractor.addFeature(new TextFeature("Type", "type", typeSize, true));
                    break;
                default:
                    System.out.println("Unknown feature: " + featureName);
            }
        }
        
        System.out.println("Feature extractor initialized with " + activeFeatures.size() + " features");
        System.out.println("Total feature size: " + featureExtractor.getTotalFeatureSize());
    }

    /**
     * Load data from JSON files.
     */
    public void loadData(String alertsFile, String situationsFile) throws IOException {
        // Load alerts
        String alertsJson = new String(Files.readAllBytes(Paths.get(alertsFile)));
        // Simplified JSON parsing - in production use a JSON library
        loadAlertsFromJson(alertsJson);
        
        // Load situations
        String situationsJson = new String(Files.readAllBytes(Paths.get(situationsFile)));
        loadSituationsFromJson(situationsJson);
        
        // Update enum features with learned values
        updateEnumFeatures();
        
        System.out.println("Loaded " + alerts.size() + " alerts and " + situations.size() + " situations");
    }

    private void loadAlertsFromJson(String json) {
        // Very simplified parsing - extract alert objects
        String[] alertStrings = json.split("\\},\\s*\\{");
        for (String alertStr : alertStrings) {
            try {
                Alert alert = new Alert();
                alert.setAlertId(extractInt(alertStr, "alert_id"));
                alert.setDescription(extractString(alertStr, "description"));
                alert.setSeverity(extractInt(alertStr, "severity"));
                alert.setSource(extractString(alertStr, "source"));
                alert.setType(extractString(alertStr, "type"));
                alert.setAgent(extractString(alertStr, "agent"));
                alert.setManager(extractString(alertStr, "manager"));
                alert.setFirstEventTime(extractLong(alertStr, "first_event_time"));
                alerts.put(alert.getAlertId(), alert);
            } catch (Exception e) {
                // Skip malformed entries
            }
        }
    }

    private void loadSituationsFromJson(String json) {
        String[] sitStrings = json.split("\\},\\s*\\{");
        for (String sitStr : sitStrings) {
            try {
                Situation sit = new Situation();
                sit.setSigId(extractInt(sitStr, "sig_id"));
                sit.setDescription(extractString(sitStr, "description"));
                sit.setCreatedDate(extractLong(sitStr, "created_date"));
                sit.setStatus(extractInt(sitStr, "status"));
                
                // Extract alert IDs (simplified)
                List<Integer> alertIds = extractIntList(sitStr, "alerts");
                for (int alertId : alertIds) {
                    if (alerts.containsKey(alertId)) {
                        sit.addAlert(alerts.get(alertId));
                    }
                }
                
                situations.put(sit.getSigId(), sit);
            } catch (Exception e) {
                // Skip malformed entries
            }
        }
    }

    private void updateEnumFeatures() {
        // Update enum features with learned values from data
        for (Feature feature : featureExtractor.getActiveFeatures()) {
            if (feature instanceof EnumFeature) {
                EnumFeature enumFeature = (EnumFeature) feature;
                String parameter = enumFeature.getParameter();
                Set<String> uniqueValues = new TreeSet<>();
                
                for (Alert alert : alerts.values()) {
                    Object value = getParameterValue(alert, parameter);
                    if (value != null) {
                        uniqueValues.add(value.toString().toLowerCase());
                    }
                }
                
                enumFeature.initializeValues(new ArrayList<>(uniqueValues));
                System.out.println("Enum feature '" + parameter + "' learned " + uniqueValues.size() + " values");
            }
        }
    }

    /**
     * Predict root causes for a situation.
     */
    public Map<Integer, Double> predictRootCauses(int sigId) {
        if (network == null) {
            System.out.println("No model available. Please train the model first.");
            return new HashMap<>();
        }
        
        Situation situation = situations.get(sigId);
        if (situation == null) {
            System.out.println("Situation not found: " + sigId);
            return new HashMap<>();
        }
        
        List<Alert> situationAlerts = situation.getAlerts();
        double[][] features = featureExtractor.extractFeatures(situationAlerts);
        double[] predictions = network.predict(features);
        
        Map<Integer, Double> results = new HashMap<>();
        for (int i = 0; i < situationAlerts.size(); i++) {
            results.put(situationAlerts.get(i).getAlertId(), predictions[i]);
        }
        
        return results;
    }

    /**
     * Display top N probable root causes for a situation.
     */
    public void displayTopRootCauses(int sigId, int topN) {
        Map<Integer, Double> predictions = predictRootCauses(sigId);
        if (predictions.isEmpty()) {
            return;
        }
        
        // Sort by probability (descending)
        List<Map.Entry<Integer, Double>> sorted = new ArrayList<>(predictions.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        Situation situation = situations.get(sigId);
        System.out.println("\n=== Top " + Math.min(topN, sorted.size()) + " Probable Root Causes for Situation " + sigId + " ===");
        System.out.println("Description: " + situation.getDescription());
        System.out.println("Total alerts: " + situation.getAlertCount());
        System.out.println();
        
        for (int i = 0; i < Math.min(topN, sorted.size()); i++) {
            Map.Entry<Integer, Double> entry = sorted.get(i);
            Alert alert = alerts.get(entry.getKey());
            double probability = entry.getValue() * 100;
            System.out.printf("%d. Alert ID: %d (%.1f%%)%n", i + 1, entry.getKey(), probability);
            System.out.println("   Description: " + alert.getDescription());
            System.out.println("   Severity: " + alert.getSeverity() + ", Host: " + alert.getSource());
            System.out.println();
        }
    }

    /**
     * Add a user feedback label.
     */
    public void addLabel(int alertId, int sigId, PrcLabel.LabelType label, String userId) {
        labels.add(new PrcLabel(alertId, sigId, label, userId));
        System.out.println("Added label: Alert " + alertId + " in Situation " + sigId + " = " + label);
    }

    /**
     * Train the model with current labels.
     */
    public void trainModel(boolean fullRetrain) {
        if (labels.isEmpty()) {
            System.out.println("No labels available for training. Please add labels first.");
            return;
        }
        
        // Prepare training data
        List<TrainingData> trainingData = new ArrayList<>();
        for (PrcLabel label : labels) {
            if (label.getLabel() == PrcLabel.LabelType.UNLABELLED) {
                continue; // Skip unlabeled data
            }
            
            Alert alert = alerts.get(label.getAlertId());
            if (alert == null) continue;
            
            double[] features = featureExtractor.extractFeatures(alert);
            double labelValue = label.toNumericValue();
            trainingData.add(new TrainingData(features, labelValue, label.getAlertId(), label.getSigId()));
        }
        
        if (trainingData.isEmpty()) {
            System.out.println("No valid training data (all labels are UNLABELLED)");
            return;
        }
        
        // Create network if it doesn't exist
        if (network == null) {
            int featureSize = featureExtractor.getTotalFeatureSize();
            network = new SimpleNeuralNetwork(featureSize, configManager.getHiddenLayerSize(), 1);
            trainer = new Trainer(network, configManager.getLearningRate(), configManager.getMaxEpochs());
        }
        
        // Train
        trainer.setVerbose(true);
        System.out.println("\n=== Starting " + (fullRetrain ? "Full" : "Incremental") + " Training ===");
        System.out.println("Training examples: " + trainingData.size());
        
        if (fullRetrain) {
            trainer.fullRetrain(trainingData);
        } else {
            trainer.incrementalTrain(trainingData);
        }
        
        System.out.println("Training completed.");
    }

    /**
     * Save the current model.
     */
    public void saveModel() throws IOException {
        if (network == null) {
            System.out.println("No model to save.");
            return;
        }
        modelDAO.saveModel(network, DEFAULT_MODEL_NAME);
    }

    /**
     * Load a saved model.
     */
    public void loadModel() throws IOException {
        if (!modelDAO.modelExists(DEFAULT_MODEL_NAME)) {
            System.out.println("No saved model found.");
            return;
        }
        network = modelDAO.loadModel(DEFAULT_MODEL_NAME);
        trainer = new Trainer(network, configManager.getLearningRate(), configManager.getMaxEpochs());
        System.out.println("Model loaded successfully.");
    }

    /**
     * Display current configuration.
     */
    public void showConfig() {
        configManager.displayConfig();
    }

    // Helper methods for JSON parsing (simplified)
    private int extractInt(String str, String key) {
        int start = str.indexOf("\"" + key + "\"");
        if (start == -1) return 0;
        start = str.indexOf(":", start) + 1;
        int end = str.indexOf(",", start);
        if (end == -1) end = str.indexOf("}", start);
        try {
            return Integer.parseInt(str.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long extractLong(String str, String key) {
        int start = str.indexOf("\"" + key + "\"");
        if (start == -1) return 0;
        start = str.indexOf(":", start) + 1;
        int end = str.indexOf(",", start);
        if (end == -1) end = str.indexOf("}", start);
        try {
            return Long.parseLong(str.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String extractString(String str, String key) {
        int start = str.indexOf("\"" + key + "\"");
        if (start == -1) return "";
        start = str.indexOf(":", start) + 1;
        int end = str.indexOf(",", start);
        if (end == -1) end = str.indexOf("}", start);
        return str.substring(start, end).trim().replaceAll("\"", "");
    }

    private List<Integer> extractIntList(String str, String key) {
        List<Integer> result = new ArrayList<>();
        int start = str.indexOf("\"" + key + "\"");
        if (start == -1) return result;
        start = str.indexOf("[", start) + 1;
        int end = str.indexOf("]", start);
        String listStr = str.substring(start, end);
        String[] items = listStr.split(",");
        for (String item : items) {
            try {
                result.add(Integer.parseInt(item.trim()));
            } catch (NumberFormatException e) {
                // Skip
            }
        }
        return result;
    }

    private Object getParameterValue(Alert alert, String parameter) {
        switch (parameter.toLowerCase()) {
            case "agent": return alert.getAgent();
            case "manager": return alert.getManager();
            default: return null;
        }
    }
}
