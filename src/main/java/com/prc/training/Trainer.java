package com.prc.training;

import com.prc.neuralnet.SimpleNeuralNetwork;
import java.util.ArrayList;
import java.util.List;

/**
 * Trains the neural network using gradient descent.
 * Implements both full retraining and incremental training strategies.
 */
public class Trainer {
    private SimpleNeuralNetwork network;
    private double learningRate;
    private int maxEpochs;
    private boolean verbose;

    public Trainer(SimpleNeuralNetwork network, double learningRate, int maxEpochs) {
        this.network = network;
        this.learningRate = learningRate;
        this.maxEpochs = maxEpochs;
        this.verbose = false;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Full retraining: reinitialize weights and train on all data.
     */
    public void fullRetrain(List<TrainingData> trainingData) {
        // Reinitialize weights (fresh start)
        reinitializeWeights();

        // Fit normalizer on all training data
        double[][] features = extractFeatures(trainingData);
        network.fitNormalizer(features);

        // Train
        train(trainingData);
    }

    /**
     * Incremental training: continue training from existing weights.
     */
    public void incrementalTrain(List<TrainingData> newTrainingData) {
        // Don't reinitialize weights - continue from current state
        // Fit normalizer on new data (or could update existing normalizer)
        double[][] features = extractFeatures(newTrainingData);
        network.fitNormalizer(features);

        // Train
        train(newTrainingData);
    }

    /**
     * Main training loop using gradient descent.
     */
    private void train(List<TrainingData> trainingData) {
        int epoch = 0;
        double previousLoss = Double.MAX_VALUE;

        while (epoch < maxEpochs) {
            double totalLoss = 0.0;
            int correctPredictions = 0;

            // Process each training example
            for (TrainingData example : trainingData) {
                double loss = trainOnExample(example);
                totalLoss += loss;

                // Count correct predictions (using 0.5 threshold)
                double prediction = network.predict(example.getFeatures());
                if ((example.getLabel() >= 0.5 && prediction >= 0.5) ||
                    (example.getLabel() < 0.5 && prediction < 0.5)) {
                    correctPredictions++;
                }
            }

            double avgLoss = totalLoss / trainingData.size();
            double accuracy = (double) correctPredictions / trainingData.size();

            if (verbose) {
                System.out.printf("Epoch %d: Loss=%.4f, Accuracy=%.2f%%%n", 
                    epoch, avgLoss, accuracy * 100);
            }

            // Early stopping if loss is very small
            if (avgLoss < 0.001) {
                if (verbose) {
                    System.out.println("Early stopping: loss converged");
                }
                break;
            }

            epoch++;
        }
    }

    /**
     * Train on a single example using backpropagation.
     */
    private double trainOnExample(TrainingData example) {
        double[] input = example.getFeatures();
        double target = example.getLabel();

        // Forward pass
        double prediction = forwardPass(input);

        // Calculate loss (mean squared error)
        double loss = Math.pow(prediction - target, 2);

        // Backward pass (gradient descent)
        backpropagate(input, target, prediction);

        return loss;
    }

    /**
     * Forward pass - returns prediction and caches intermediate values.
     * For simplicity, we'll call network.predict() and re-compute for backprop.
     */
    private double forwardPass(double[] input) {
        return network.predict(input);
    }

    /**
     * Backpropagation - update weights based on error.
     * This is a simplified implementation.
     */
    private void backpropagate(double[] input, double target, double prediction) {
        // Normalize input
        double[] normalizedInput = network.getNormaliser().normalize(input);

        // Get current weights
        double[][] w1 = network.getWeights1();
        double[] b1 = network.getBias1();
        double[][] w2 = network.getWeights2();
        double[] b2 = network.getBias2();

        int hiddenSize = network.getHiddenSize();
        int inputSize = network.getInputSize();

        // Recompute hidden layer (needed for gradients)
        double[] hidden = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = b1[i];
            for (int j = 0; j < inputSize; j++) {
                sum += w1[i][j] * normalizedInput[j];
            }
            hidden[i] = 1.0 / (1.0 + Math.exp(-sum)); // Sigmoid
        }

        // Output layer gradient
        double outputError = prediction - target;
        double outputDelta = outputError * prediction * (1.0 - prediction); // Sigmoid derivative

        // Hidden layer gradients
        double[] hiddenDeltas = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            hiddenDeltas[i] = w2[0][i] * outputDelta * hidden[i] * (1.0 - hidden[i]);
        }

        // Update output layer weights and bias
        for (int i = 0; i < hiddenSize; i++) {
            w2[0][i] -= learningRate * outputDelta * hidden[i];
        }
        b2[0] -= learningRate * outputDelta;

        // Update hidden layer weights and biases
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                w1[i][j] -= learningRate * hiddenDeltas[i] * normalizedInput[j];
            }
            b1[i] -= learningRate * hiddenDeltas[i];
        }

        // Set updated weights back to network
        network.setWeights1(w1);
        network.setBias1(b1);
        network.setWeights2(w2);
        network.setBias2(b2);
    }

    /**
     * Extract feature vectors from training data.
     */
    private double[][] extractFeatures(List<TrainingData> trainingData) {
        double[][] features = new double[trainingData.size()][];
        for (int i = 0; i < trainingData.size(); i++) {
            features[i] = trainingData.get(i).getFeatures();
        }
        return features;
    }

    /**
     * Reinitialize network weights (for full retraining).
     */
    private void reinitializeWeights() {
        // Create a new network with same dimensions to reinitialize weights
        SimpleNeuralNetwork newNetwork = new SimpleNeuralNetwork(
            network.getInputSize(),
            network.getHiddenSize(),
            network.getOutputSize()
        );

        // Copy the new weights to current network
        network.setWeights1(newNetwork.getWeights1());
        network.setBias1(newNetwork.getBias1());
        network.setWeights2(newNetwork.getWeights2());
        network.setBias2(newNetwork.getBias2());
    }
}
