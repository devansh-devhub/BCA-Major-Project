package com.prc.persistence;

import com.prc.neuralnet.Normaliser;
import com.prc.neuralnet.SimpleNeuralNetwork;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Handles model persistence - saving and loading trained models.
 * Uses JSON serialization for simplicity (in production, Moogsoft uses database).
 */
public class ModelDAO {
    private String modelsDirectory;

    public ModelDAO(String modelsDirectory) {
        this.modelsDirectory = modelsDirectory;
        // Create directory if it doesn't exist
        File dir = new File(modelsDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Save a trained model to disk.
     */
    public void saveModel(SimpleNeuralNetwork network, String modelName) throws IOException {
        String filePath = modelsDirectory + File.separator + modelName + ".json";
        
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"inputSize\": ").append(network.getInputSize()).append(",\n");
        json.append("  \"hiddenSize\": ").append(network.getHiddenSize()).append(",\n");
        json.append("  \"outputSize\": ").append(network.getOutputSize()).append(",\n");
        
        // Save weights1
        json.append("  \"weights1\": [");
        double[][] w1 = network.getWeights1();
        for (int i = 0; i < w1.length; i++) {
            json.append("[");
            for (int j = 0; j < w1[i].length; j++) {
                json.append(w1[i][j]);
                if (j < w1[i].length - 1) json.append(", ");
            }
            json.append("]");
            if (i < w1.length - 1) json.append(", ");
        }
        json.append("],\n");
        
        // Save bias1
        json.append("  \"bias1\": [");
        double[] b1 = network.getBias1();
        for (int i = 0; i < b1.length; i++) {
            json.append(b1[i]);
            if (i < b1.length - 1) json.append(", ");
        }
        json.append("],\n");
        
        // Save weights2
        json.append("  \"weights2\": [");
        double[][] w2 = network.getWeights2();
        for (int i = 0; i < w2.length; i++) {
            json.append("[");
            for (int j = 0; j < w2[i].length; j++) {
                json.append(w2[i][j]);
                if (j < w2[i].length - 1) json.append(", ");
            }
            json.append("]");
            if (i < w2.length - 1) json.append(", ");
        }
        json.append("],\n");
        
        // Save bias2
        json.append("  \"bias2\": [");
        double[] b2 = network.getBias2();
        for (int i = 0; i < b2.length; i++) {
            json.append(b2[i]);
            if (i < b2.length - 1) json.append(", ");
        }
        json.append("],\n");
        
        // Save normalizer
        Normaliser normaliser = network.getNormaliser();
        json.append("  \"normalizer\": {\n");
        json.append("    \"means\": [");
        double[] means = normaliser.getMeans();
        for (int i = 0; i < means.length; i++) {
            json.append(means[i]);
            if (i < means.length - 1) json.append(", ");
        }
        json.append("],\n");
        
        json.append("    \"stds\": [");
        double[] stds = normaliser.getStds();
        for (int i = 0; i < stds.length; i++) {
            json.append(stds[i]);
            if (i < stds.length - 1) json.append(", ");
        }
        json.append("],\n");
        
        json.append("    \"sampleCount\": ").append(normaliser.getSampleCount()).append("\n");
        json.append("  }\n");
        
        json.append("}\n");
        
        Files.write(Paths.get(filePath), json.toString().getBytes());
        System.out.println("Model saved to: " + filePath);
    }

    /**
     * Load a trained model from disk.
     */
    public SimpleNeuralNetwork loadModel(String modelName) throws IOException {
        String filePath = modelsDirectory + File.separator + modelName + ".json";
        
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        
        // Parse JSON (simplified parsing - in production use a JSON library)
        int inputSize = extractInt(content, "\"inputSize\":");
        int hiddenSize = extractInt(content, "\"hiddenSize\":");
        int outputSize = extractInt(content, "\"outputSize\":");
        
        SimpleNeuralNetwork network = new SimpleNeuralNetwork(inputSize, hiddenSize, outputSize);
        
        // Extract and set weights (simplified - would use proper JSON parser in production)
        double[][] w1 = extract2DArray(content, "\"weights1\":", hiddenSize, inputSize);
        double[] b1 = extract1DArray(content, "\"bias1\":", hiddenSize);
        double[][] w2 = extract2DArray(content, "\"weights2\":", outputSize, hiddenSize);
        double[] b2 = extract1DArray(content, "\"bias2\":", outputSize);
        
        network.setWeights1(w1);
        network.setBias1(b1);
        network.setWeights2(w2);
        network.setBias2(b2);
        
        // Extract and set normalizer
        double[] means = extract1DArray(content, "\"means\":", inputSize);
        double[] stds = extract1DArray(content, "\"stds\":", inputSize);
        int sampleCount = extractInt(content, "\"sampleCount\":");
        
        Normaliser normaliser = new Normaliser(inputSize);
        // Set the values directly (would need setter methods in production)
        // For now, we'll just re-fit when loading
        
        System.out.println("Model loaded from: " + filePath);
        return network;
    }

    /**
     * Check if a model file exists.
     */
    public boolean modelExists(String modelName) {
        String filePath = modelsDirectory + File.separator + modelName + ".json";
        return new File(filePath).exists();
    }

    // Simplified JSON extraction methods (for learning purposes)
    private int extractInt(String content, String key) {
        int start = content.indexOf(key) + key.length();
        int end = content.indexOf(",", start);
        if (end == -1) end = content.indexOf("}", start);
        return Integer.parseInt(content.substring(start, end).trim());
    }

    private double[] extract1DArray(String content, String key, int size) {
        int start = content.indexOf(key) + key.length();
        int end = content.indexOf("]", start);
        String arrayContent = content.substring(start + 1, end);
        String[] values = arrayContent.split(",");
        double[] result = new double[size];
        for (int i = 0; i < Math.min(size, values.length); i++) {
            result[i] = Double.parseDouble(values[i].trim());
        }
        return result;
    }

    private double[][] extract2DArray(String content, String key, int rows, int cols) {
        int start = content.indexOf(key) + key.length();
        int end = content.indexOf("]", start);
        // Find the matching closing bracket for the 2D array
        int bracketCount = 0;
        int i = start;
        while (i < content.length()) {
            if (content.charAt(i) == '[') bracketCount++;
            if (content.charAt(i) == ']') bracketCount--;
            if (bracketCount == 0) {
                end = i;
                break;
            }
            i++;
        }
        
        String arrayContent = content.substring(start + 1, end);
        String[] rowStrings = arrayContent.split("\\],\\s*\\[");
        
        double[][] result = new double[rows][cols];
        for (int r = 0; r < Math.min(rows, rowStrings.length); r++) {
            String row = rowStrings[r].replaceAll("[\\[\\]]", "").trim();
            String[] values = row.split(",");
            for (int c = 0; c < Math.min(cols, values.length); c++) {
                result[r][c] = Double.parseDouble(values[c].trim());
            }
        }
        return result;
    }
}
