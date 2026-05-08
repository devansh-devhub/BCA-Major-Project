package com.prc.neuralnet;

/**
 * Sigmoid activation function.
 * Implements: sigmoid(z) = 1 / (1 + e^(-z))
 */
public class Sigmoid {

    /**
     * Compute sigmoid for a single value.
     */
    public static double compute(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    /**
     * Compute sigmoid for an array (element-wise).
     */
    public static double[] compute(double[] z) {
        double[] result = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            result[i] = compute(z[i]);
        }
        return result;
    }

    /**
     * Compute sigmoid gradient: sigmoid'(z) = sigmoid(z) * (1 - sigmoid(z))
     * Used in backpropagation.
     */
    public static double computeGradient(double z) {
        double sig = compute(z);
        return sig * (1.0 - sig);
    }

    /**
     * Compute sigmoid gradient for an array (element-wise).
     */
    public static double[] computeGradient(double[] z) {
        double[] result = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            result[i] = computeGradient(z[i]);
        }
        return result;
    }
}
