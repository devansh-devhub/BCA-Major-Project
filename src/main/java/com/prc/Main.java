package com.prc;

import com.prc.model.PrcLabel;

import java.io.IOException;
import java.util.Scanner;

/**
 * Command-line interface for the PRC learning project.
 * Provides interactive commands for prediction, labeling, training, and model management.
 */
public class Main {
    private static PrcEngine engine;
    private static final Scanner scanner = new Scanner(System.in);
    
    private static final String CONFIG_PATH = "config/prc_config.json";
    private static final String MODELS_DIR = "models";
    private static final String ALERTS_FILE = "data/sample_alerts.json";
    private static final String SITUATIONS_FILE = "data/sample_situations.json";

    public static void main(String[] args) {
        System.out.println("=== PRC Learning Project ===");
        System.out.println("A simplified implementation of Moogsoft Probable Root Cause system\n");
        
        try {
            // Initialize engine
            System.out.println("Initializing PRC Engine...");
            engine = new PrcEngine(CONFIG_PATH, MODELS_DIR);
            
            // Load sample data
            System.out.println("Loading sample data...");
            engine.loadData(ALERTS_FILE, SITUATIONS_FILE);
            
            System.out.println("\nInitialization complete!\n");
            
            // Command loop
            commandLoop();
            
        } catch (IOException e) {
            System.err.println("Error initializing PRC Engine: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void commandLoop() {
        while (true) {
            System.out.print("\nprc> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();
            
            try {
                switch (command) {
                    case "predict":
                        handlePredict(parts);
                        break;
                    case "label":
                        handleLabel(parts);
                        break;
                    case "train":
                        handleTrain(parts);
                        break;
                    case "retrain":
                        handleRetrain(parts);
                        break;
                    case "save-model":
                        handleSaveModel();
                        break;
                    case "load-model":
                        handleLoadModel();
                        break;
                    case "show-config":
                        handleShowConfig();
                        break;
                    case "help":
                        showHelp();
                        break;
                    case "exit":
                    case "quit":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Unknown command: " + command);
                        System.out.println("Type 'help' for available commands");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void handlePredict(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: predict <situation_id> [top_n]");
            System.out.println("Example: predict 1 3");
            return;
        }
        
        try {
            int sigId = Integer.parseInt(parts[1]);
            int topN = parts.length > 2 ? Integer.parseInt(parts[2]) : 3;
            
            engine.displayTopRootCauses(sigId, topN);
        } catch (NumberFormatException e) {
            System.out.println("Invalid situation ID. Please provide a number.");
        }
    }

    private static void handleLabel(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Usage: label <situation_id> <alert_id> <label_type> [user_id]");
            System.out.println("Label types: ROOT_CAUSE, NON_CAUSAL, UNLABELLED");
            System.out.println("Example: label 1 1 ROOT_CAUSE admin");
            return;
        }
        
        try {
            int sigId = Integer.parseInt(parts[1]);
            int alertId = Integer.parseInt(parts[2]);
            String labelTypeStr = parts[3].toUpperCase();
            String userId = parts.length > 4 ? parts[4] : "admin";
            
            PrcLabel.LabelType labelType;
            switch (labelTypeStr) {
                case "ROOT_CAUSE":
                    labelType = PrcLabel.LabelType.ROOT_CAUSE;
                    break;
                case "NON_CAUSAL":
                    labelType = PrcLabel.LabelType.NON_CAUSAL;
                    break;
                case "UNLABELLED":
                    labelType = PrcLabel.LabelType.UNLABELLED;
                    break;
                default:
                    System.out.println("Invalid label type. Use: ROOT_CAUSE, NON_CAUSAL, or UNLABELLED");
                    return;
            }
            
            engine.addLabel(alertId, sigId, labelType, userId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid IDs. Please provide numbers for situation_id and alert_id.");
        }
    }

    private static void handleTrain(String[] parts) {
        System.out.println("Performing incremental training...");
        engine.trainModel(false);
    }

    private static void handleRetrain(String[] parts) {
        System.out.println("Performing full retraining...");
        engine.trainModel(true);
    }

    private static void handleSaveModel() throws IOException {
        engine.saveModel();
    }

    private static void handleLoadModel() throws IOException {
        engine.loadModel();
    }

    private static void handleShowConfig() {
        engine.showConfig();
    }

    private static void showHelp() {
        System.out.println("\n=== Available Commands ===");
        System.out.println("predict <situation_id> [top_n]");
        System.out.println("    Predict root causes for a situation");
        System.out.println("    Example: predict 1 3");
        System.out.println();
        System.out.println("label <situation_id> <alert_id> <label_type> [user_id]");
        System.out.println("    Add user feedback label for an alert");
        System.out.println("    Label types: ROOT_CAUSE, NON_CAUSAL, UNLABELLED");
        System.out.println("    Example: label 1 1 ROOT_CAUSE admin");
        System.out.println();
        System.out.println("train");
        System.out.println("    Perform incremental training with existing labels");
        System.out.println();
        System.out.println("retrain");
        System.out.println("    Perform full retraining (reinitialize weights)");
        System.out.println();
        System.out.println("save-model");
        System.out.println("    Save the current model to disk");
        System.out.println();
        System.out.println("load-model");
        System.out.println("    Load a saved model from disk");
        System.out.println();
        System.out.println("show-config");
        System.out.println("    Display current configuration");
        System.out.println();
        System.out.println("help");
        System.out.println("    Show this help message");
        System.out.println();
        System.out.println("exit");
        System.out.println("    Exit the program");
        System.out.println("=========================\n");
    }
}
