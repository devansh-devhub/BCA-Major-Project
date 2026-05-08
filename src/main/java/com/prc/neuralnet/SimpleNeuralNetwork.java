package com.prc.neuralnet;

import java.util.Random;

/**
 * Simplified 3-layer neural network for PRC.
 * Architecture: Input -> Hidden (sigmoid) -> Output (sigmoid)
 * 
 * This is a simplified implementation using arrays instead of matrix libraries
 * for learning purposes. In production, Moogsoft uses jblas for efficiency.
 */
public class SimpleNeuralNetwork {
    private int inputSize;
    private int hiddenSize;
    private int outputSize;

    // Weights and biases
    private double[][] weights1; // Input -> Hidden
    private double[] bias1;
    private double[][] weights2; // Hidden -> Output
    private double[] bias2;

    private Normaliser normaliser;
    private Random random;

    public SimpleNeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.normaliser = new Normaliser(inputSize);
        this.random = new Random(42); // Fixed seed for reproducibility

        initializeWeights();
    }

    /**
     * Initialize weights with small random values.
     * Uses Xavier initialization approximation.
     */
    private void initializeWeights() {
        // Input -> Hidden layer
        weights1 = new double[hiddenSize][inputSize];
        bias1 = new double[hiddenSize];
        double scale1 = Math.sqrt(2.0 / inputSize);
        
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                weights1[i][j] = random.nextGaussian() * scale1;
            }
            bias1[i] = 0.0;
        }

        // Hidden -> Output layer
        weights2 = new double[outputSize][hiddenSize];
        bias2 = new double[outputSize];
        double scale2 = Math.sqrt(2.0 / hiddenSize);
        
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weights2[i][j] = random.nextGaussian() * scale2;
            }
            bias2[i] = 0.0;
        }
    }

    /**
     * Forward propagation: predict output for given input.
     */
    public double predict(double[] input) {
        // Normalize input
        double[] normalizedInput = normaliser.normalize(input);

        // Hidden layer
        double[] hidden = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = bias1[i];
            for (int j = 0; j < inputSize; j++) {
                sum += weights1[i][j] * normalizedInput[j];
            }
            hidden[i] = Sigmoid.compute(sum);
        }

        // Output layer
        double output = bias2[0];
        for (int i = 0; i < hiddenSize; i++) {
            output += weights2[0][i] * hidden[i];
        }
        output = Sigmoid.compute(output);

        return output;
    }

    /**
     * Predict for multiple inputs.
     */
    public double[] predict(double[][] inputs) {
        double[] predictions = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            predictions[i] = predict(inputs[i]);
        }
        return predictions;
    }

    /**
     * Fit the normalizer on training data.
     */
    public void fitNormalizer(double[][] features) {
        normaliser.fit(features);
    }

    /**
     * Get the normalizer for external use (e.g., persistence).
     */
    public Normaliser getNormaliser() {
        return normaliser;
    }

    /**
     * Set the normalizer (e.g., when loading from persistence).
     */
    public void setNormaliser(Normaliser normaliser) {
        this.normaliser = normaliser;
    }

    // Getters for weights (for persistence)
    public double[][] getWeights1() {
        return weights1;
    }

    public double[] getBias1() {
        return bias1;
    }

    public double[][] getWeights2() {
        return weights2;
    }

    public double[] getBias2() {
        return bias2;
    }

    // Setters for weights (for loading from persistence)
    public void setWeights1(double[][] weights1) {
        this.weights1 = weights1;
    }

    public void setBias1(double[] bias1) {
        this.bias1 = bias1;
    }

    public void setWeights2(double[][] weights2) {
        this.weights2 = weights2;
    }

    public void setBias2(double[] bias2) {
        this.bias2 = bias2;
    }

    public int getInputSize() {
        return inputSize;
    }

    public int getHiddenSize() {
        return hiddenSize;
    }

    public int getOutputSize() {
        return outputSize;
    }
}
