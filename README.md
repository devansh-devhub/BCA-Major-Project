# PRC Learning Project

A standalone Java implementation of Moogsoft's Probable Root Cause (PRC) system for learning purposes.

## Overview

This project implements the core concepts of Moogsoft PRC in a simplified, beginner-friendly manner. It demonstrates how machine learning is used to identify root cause alerts within situations through feature extraction, neural networks, and user feedback.

## Moogsoft Production Correlation

This learning project directly mirrors the architecture and components of the production Moogsoft PRC system. Each component in this project corresponds to specific Moogsoft production files and classes:

| Learning Project Component | Moogsoft Production File/Class | Location in Moogsoft Codebase |
|---------------------------|------------------------------|-------------------------------|
| **Feature Extraction** | | |
| Feature.java | CAbstractFeature | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CAbstractFeature.java |
| FeatureExtractor.java | CFeatureExtractor | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CFeatureExtractor.java |
| TextFeature.java | CRawTextFeature | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CRawTextFeature.java |
| NumericFeature.java | CNumericFeature | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CNumericFeature.java |
| EnumFeature.java | CEnumeratedTextFeature | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CEnumeratedTextFeature.java |
| **Neural Network** | | |
| SimpleNeuralNetwork.java | CTrainedModel | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CTrainedModel.java |
| Sigmoid.java | CSigmoid | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CSigmoid.java |
| Normaliser.java | CNormaliser | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CNormaliser.java |
| **Training** | | |
| Trainer.java | CFullRetrain / CIncrementalRetrain | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CFullRetrain.java |
| TrainingData.java | CTrainingData | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CTrainingData.java |
| **Persistence** | | |
| ModelDAO.java | CModelDAO | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CModelDAO.java |
| ConfigManager.java | Configuration loading from situation_root_cause.conf | farmd/moog_farmd/src/main/resources/config/moolets/situation_root_cause.conf |
| **Core Engine** | | |
| PrcEngine.java | CSituationProbableRootCause | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CSituationProbableRootCause.java |
| PrcEngine.java | CPrcSingletonStore | farmd/moolets/rootcause/src/main/java/com/moogsoft/farmd/moolet/rootcause/CPrcSingletonStore.java |
| **API Endpoints** | | |
| CLI predict command | Graze CGetTopPrcDetails | servlets/graze/src/main/java/com/moogsoft/graze/CGetTopPrcDetails.java |
| CLI label command | Graze CSetPrcLabels | servlets/graze/src/main/java/com/moogsoft/graze/CSetPrcLabels.java |
| CLI retrain command | MooServlet CRetrainPrc | servlets/moogsvr/src/main/java/com/moogsoft/moogsvr/CRetrainPrc.java |
| **UI Components** | | |
| (Not implemented) | CPrcGridWidget | ui/src/core/app/operationalData/situations/prc/CPrcGridWidget.js |
| (Not implemented) | NextStepsPrc (Vue.js) | ui/src/core/app/operationalData/situations/room/nextStepsTab/prc/NextStepsPrc.js |
| **Database** | | |
| JSON file persistence | prc_models table | common/database/moog_sigdb/tables/prc_models.sql |
| **Configuration** | | |
| prc_config.json | situation_root_cause.conf | farmd/moog_farmd/src/main/resources/config/moolets/situation_root_cause.conf |
| **Message Topics** | | |
| (Not implemented) | RC_TRAIN_ALL_SITNS | Internal message topic for full retraining |
| (Not implemented) | RC_INC_TRAIN_SITNS | Internal message topic for incremental training |
| (Not implemented) | RC_ASSIGN_SITNS | Internal message topic for assigning predictions |

## Project Structure

```
prc-learning-project/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── prc/
│                   ├── model/              # Data models
│                   │   ├── Alert.java
│                   │   ├── Situation.java
│                   │   └── PrcLabel.java
│                   ├── features/            # Feature extraction
│                   │   ├── Feature.java              # Moogsoft: CAbstractFeature
│                   │   ├── FeatureExtractor.java     # Moogsoft: CFeatureExtractor
│                   │   ├── TextFeature.java          # Moogsoft: CRawTextFeature
│                   │   ├── NumericFeature.java       # Moogsoft: CNumericFeature
│                   │   └── EnumFeature.java          # Moogsoft: CEnumeratedTextFeature
│                   ├── neuralnet/           # Neural network
│                   │   ├── SimpleNeuralNetwork.java  # Moogsoft: CTrainedModel
│                   │   ├── Sigmoid.java              # Moogsoft: CSigmoid
│                   │   └── Normaliser.java           # Moogsoft: CNormaliser
│                   ├── training/            # Training
│                   │   ├── Trainer.java              # Moogsoft: CFullRetrain / CIncrementalRetrain
│                   │   └── TrainingData.java         # Moogsoft: CTrainingData
│                   ├── persistence/         # Model persistence
│                   │   ├── ModelDAO.java             # Moogsoft: CModelDAO
│                   │   └── ConfigManager.java        # Moogsoft: situation_root_cause.conf
│                   ├── PrcEngine.java       # Main coordinator
│                   │                          # Moogsoft: CSituationProbableRootCause
│                   │                          # Moogsoft: CPrcSingletonStore
│                   └── Main.java            # CLI interface
│                                      # Moogsoft: Graze APIs (CGetTopPrcDetails, CSetPrcLabels)
│                                      # Moogsoft: MooServlet CRetrainPrc
├── data/
│   ├── sample_alerts.json       # Sample alert data
│   └── sample_situations.json  # Sample situation data
├── config/
│   └── prc_config.json         # Configuration file
│                              # Moogsoft: situation_root_cause.conf
├── models/                      # Saved models directory
│                              # Moogsoft: prc_models table in database
└── README.md
```

## Prerequisites

- Java 11 or higher
- No external libraries required (uses standard Java library only)

## Building and Running

### Compile the Project

```bash
cd C:\prc-learning-project
javac -d bin -src\main\java\com\prc\*.java src\main\java\com\prc\**\*.java
```

### Run the Application

```bash
java -cp bin com.prc.Main
```

## Usage

### Command-Line Interface

The application provides an interactive command-line interface with the following commands:

#### predict
Predict root causes for a situation.
```
predict <situation_id> [top_n]
```
Example: `predict 1 3`

#### label
Add user feedback label for an alert.
```
label <situation_id> <alert_id> <label_type> [user_id]
```
Label types: ROOT_CAUSE, NON_CAUSAL, UNLABELLED
Example: `label 1 1 ROOT_CAUSE admin`

#### train
Perform incremental training with existing labels.
```
train
```

#### retrain
Perform full retraining (reinitialize weights).
```
retrain
```

#### save-model
Save the current model to disk.
```
save-model
```

#### load-model
Load a saved model from disk.
```
load-model
```

#### show-config
Display current configuration.
```
show-config
```

#### help
Show available commands.
```
help
```

#### exit
Exit the program.
```
exit
```

## Example Workflow

1. **Start the application**
   ```
   java -cp bin com.prc.Main
   ```

2. **View configuration**
   ```
   prc> show-config
   ```

3. **Predict root causes for a situation**
   ```
   prc> predict 1 3
   ```

4. **Add user feedback**
   ```
   prc> label 1 1 ROOT_CAUSE admin
   prc> label 1 2 NON_CAUSAL admin
   ```

5. **Train the model**
   ```
   prc> train
   ```

6. **Save the model**
   ```
   prc> save-model
   ```

7. **Predict again to see improved results**
   ```
   prc> predict 1 3
   ```

## Key Concepts Demonstrated

### 1. Feature Engineering
- **Text Features**: Tokenization with hash trick for dimensionality reduction
  - Moogsoft: CRawTextFeature with hash trick tokenization (word or shingle-based)
  - Moogsoft: CEnumeratedTextFeature for categorical text values
  - Configuration: situation_root_cause.conf defines feature name, parameter, type, interpretation
- **Numeric Features**: Normalized continuous values (e.g., severity)
  - Moogsoft: CNumericFeature with min-max normalization
  - Moogsoft: CNormaliser.fit() computes statistics, normalize() transforms values
- **Enum Features**: One-hot encoding for categorical values (e.g., agent, manager)
  - Moogsoft: CEnumeratedTextFeature maps unique values to indices
  - Moogsoft: Supports ranking parameter for relative order within situation

### 2. Neural Network
- **3-layer architecture**: Input → Hidden (sigmoid) → Output (sigmoid)
  - Moogsoft: CTrainedModel implements same architecture
  - Moogsoft: Uses jblas library for efficient matrix operations
  - Moogsoft: Weights stored as mTheta1T (input→hidden) and mTheta2T (hidden→output)
- **Forward propagation**: Computing predictions from features
  - Moogsoft: CTrainedModel.categorize() method
  - Moogsoft: Feature normalization via CNormaliser before forward pass
- **Activation functions**: Sigmoid for non-linearity
  - Moogsoft: CSigmoid.compute() and computeGradient() methods
  - Moogsoft: Same sigmoid formula: 1 / (1 + e^(-x))
- **Feature normalization**: Z-score normalization
  - Moogsoft: CNormaliser with fit() and normalize() methods
  - Moogsoft: Stores mean and std for each feature dimension

### 3. Training
- **Gradient descent**: Updating weights based on prediction error
  - Moogsoft: Uses conjugate gradient optimization (CMinCGPolakRibiere)
  - Moogsoft: More sophisticated than simple gradient descent
- **Backpropagation**: Computing gradients through the network
  - Moogsoft: Implemented in CFullRetrain and CIncrementalRetrain
  - Moogsoft: CSigmoid.computeGradient() for activation derivatives
- **Incremental vs Full Retrain**: Different training strategies
  - Moogsoft: CIncrementalRetrain updates existing weights
  - Moogsoft: CFullRetrain reinitializes weights from scratch
  - Moogsoft: Controlled by inc_train_strategy in situation_root_cause.conf
- **Loss function**: Mean squared error
  - Moogsoft: Same loss function with lambda regularization
  - Moogsoft: Lambda parameter controls regularization strength

### 4. Configuration
- **Feature selection**: Choosing which features to use
  - Moogsoft: active_features in situation_root_cause.conf
  - Moogsoft: Feature definitions with name, parameter, type, interpretation
  - Moogsoft: Experimental features can be enabled/disabled
- **Hyperparameters**: Learning rate, hidden layer size, max epochs
  - Moogsoft: hidden_layer_size, lambda, max_epochs in .conf
  - Moogsoft: Can be adjusted without code changes
- **Training strategy**: Incremental vs full retraining
  - Moogsoft: inc_train_strategy parameter in .conf
  - Moogsoft: "incremental" or "full" options

### 5. Model Persistence
- **JSON serialization**: Saving models to disk
  - Moogsoft: CModelDAO.write() serializes to JSON
  - Moogsoft: Stores in prc_models table in database
  - Moogsoft: Includes weights, biases, normalizer statistics, active features
- **Loading models**: Restoring trained models
  - Moogsoft: CModelDAO.read() deserializes from database
  - Moogsoft: CPrcSingletonStore provides thread-safe access
  - Moogsoft: Model loaded on startup via CModelDAO
- **Configuration management**: Loading/saving settings
  - Moogsoft: situation_root_cause.conf parsed on startup
  - Moogsoft: MooServlet CRetrainPrc allows reconfiguration

## Learning Outcomes

By building and using this project, you will understand:

- How raw alert data is transformed into numerical features
- How neural networks make predictions
- How user feedback improves model accuracy
- The trade-offs between incremental and full retraining
- How configuration affects predictions
- How models are persisted and loaded
- Troubleshooting common PRC issues

## Comparison with Moogsoft PRC

This simplified implementation differs from production Moogsoft PRC in several ways:

### Similarities
- **Same core concepts**: Feature extraction, neural network, training, user feedback
- **Same data model structure**: Alert, Situation, Labels (ROOT_CAUSE, NON_CAUSAL, UNLABELLED)
- **Same configuration approach**: JSON config mirrors situation_root_cause.conf structure
- **Same training strategies**: Incremental vs Full retrain (CIncrementalRetrain vs CFullRetrain)
- **Same neural network architecture**: 3-layer network with sigmoid activation
- **Same feature types**: Text (hash trick), Numeric (normalized), Enum (one-hot)

### Differences
- **No external ML libraries**: Uses simple array operations instead of jblas
  - Moogsoft: Uses jblas for efficient matrix operations in CTrainedModel
- **Simplified neural network**: Basic implementation vs production's optimized CTrainedModel
  - Moogsoft: More sophisticated weight initialization and optimization
- **File-based persistence**: JSON files instead of database
  - Moogsoft: CModelDAO stores models in prc_models table (MySQL database)
  - Moogsoft: CPrcSingletonStore provides thread-safe singleton access
- **Command-line only**: No web UI
  - Moogsoft: Graze APIs (CGetTopPrcDetails, CSetPrcLabels) for external access
  - Moogsoft: UI components (CPrcGridWidget, NextStepsPrc) for operator feedback
- **Basic features**: Fewer feature types and simpler implementations
  - Moogsoft: Supports shingles (n-character substrings), ranking parameters
  - Moogsoft: More sophisticated feature definitions in situation_root_cause.conf
- **No thread safety**: Single-threaded vs production's concurrent processing
  - Moogsoft: CPrcSingletonStore uses StampedLock for concurrent access
  - Moogsoft: Message topics (RC_TRAIN_ALL_SITNS, RC_INC_TRAIN_SITNS) for async training
- **No message-based architecture**: Direct method calls vs message passing
  - Moogsoft: Uses message topics for training triggers
  - Moogsoft: CSituationProbableRootCause subscribes to topics for coordination
- **Simplified optimization**: Gradient descent vs conjugate gradient
  - Moogsoft: Uses CMinCGPolakRibiere (conjugate gradient) for faster convergence
  - Moogsoft: Lambda regularization parameter for preventing overfitting

## Troubleshooting

### "No model available" error
- You need to train the model first using the `train` or `retrain` command
- Or load a previously saved model using `load-model`

### Poor predictions
- Ensure you have enough labeled data
- Check that labels are consistent (conflicting labels confuse the model)
- Try adjusting hyperparameters in `config/prc_config.json`
- Consider full retraining if incremental training isn't improving

### Feature extraction issues
- Verify that alert data has the required fields
- Check that feature sizes in configuration are appropriate
- Review the feature extraction logs during initialization

## Configuration

Edit `config/prc_config.json` to customize:

- **active_features**: Which features to use (Description, Severity, Host, etc.)
  - Moogsoft: Corresponds to `active_features` in situation_root_cause.conf
  - Moogsoft: Feature definitions with name, parameter, type, interpretation
- **hidden_layer_size**: Number of neurons in hidden layer
  - Moogsoft: `hidden_layer_size` parameter in situation_root_cause.conf
  - Moogsoft: Affects network architecture and model capacity
- **learning_rate**: Gradient descent learning rate
  - Moogsoft: Not directly in .conf (uses conjugate gradient optimization)
  - Moogsoft: Learning rate is determined by CMinCGPolakRibiere optimizer
- **max_epochs**: Maximum training iterations
  - Moogsoft: `max_epochs` parameter in situation_root_cause.conf
  - Moogsoft: Experimental parameter for controlling training duration
- **training_strategy**: "incremental" or "full" (for auto-selection)
  - Moogsoft: `inc_train_strategy` parameter in situation_root_cause.conf
  - Moogsoft: Controls whether to use CIncrementalRetrain or CFullRetrain
- **feature_sizes**: Hash space size for text features
  - Moogsoft: Defined in feature definitions (parameter field)
  - Moogsoft: For example, Description feature might use parameter "256"

## Extending the Project

Ideas for further learning with Moogsoft parallels:

1. **Add more feature types**: Time features, ensemble features
   - Moogsoft: Supports time-based features and ensemble combinations
   - Moogsoft: Feature factory pattern in CFeatureFactory

2. **Implement shingles**: N-character substring tokenization
   - Moogsoft: CRawTextFeature supports both "word" and "shingle" tokenization
   - Moogsoft: Shingle size configurable in feature parameter

3. **Add conjugate gradient optimization**: Like Moogsoft's CMinCGPolakRibiere
   - Moogsoft: More efficient than simple gradient descent
   - Moogsoft: Faster convergence with fewer iterations

4. **Implement proper JSON parsing**: Use Jackson or Gson library
   - Moogsoft: Uses production-grade JSON libraries
   - Moogsoft: More robust parsing of complex nested structures

5. **Add database persistence**: Store models in MySQL like Moogsoft
   - Moogsoft: CModelDAO stores in prc_models table
   - Moogsoft: Database schema: common/database/moog_sigdb/tables/prc_models.sql
   - Moogsoft: Includes model name, updated timestamp, diagnostics, model JSON

6. **Add thread safety**: Implement StampedLock like CPrcSingletonStore
   - Moogsoft: CPrcSingletonStore uses StampedLock for concurrent access
   - Moogsoft: Thread-safe singleton pattern for model and extractor
   - Moogsoft: Multiple threads can read simultaneously, exclusive write access

7. **Add web UI**: Simple REST API and frontend
   - Moogsoft: Graze APIs (CGetTopPrcDetails, CSetPrcLabels) for external access
   - Moogsoft: UI components (CPrcGridWidget, NextStepsPrc) for operator feedback
   - Moogsoft: MooServlet endpoints for retraining (CRetrainPrc)

8. **Add more metrics**: Training accuracy, precision, recall
   - Moogsoft: Diagnostics stored in prc_models table
   - Moogsoft: Training statistics and performance metrics

9. **Implement message-based architecture**: Use message topics for coordination
   - Moogsoft: RC_TRAIN_ALL_SITNS topic for full retraining
   - Moogsoft: RC_INC_TRAIN_SITNS topic for incremental training
   - Moogsoft: RC_ASSIGN_SITNS topic for prediction assignment
   - Moogsoft: CSituationProbableRootCause subscribes to topics

10. **Add lambda regularization**: Prevent overfitting
    - Moogsoft: Lambda parameter in situation_root_cause.conf
    - Moogsoft: Regularization term in loss function
    - Moogsoft: Helps prevent overfitting to training data

## License

This is a learning project for educational purposes. The original Moogsoft code is proprietary.

## Acknowledgments

This project is inspired by Moogsoft's Probable Root Cause system and is designed to help engineers understand the core concepts used in production.
