package main.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class InputPane extends VBox {
    private TextArea codeInputArea;
    private RadioButton singleInputRadio;
    private RadioButton rangeInputRadio;
    
    private RadioButton manualInputRadio;
    private RadioButton randomInputRadio;
    private RadioButton hardcodedInputRadio;
    
    private ComboBox<String> arrayTypeComboBox;
    
    private TextField inputSizeField;
    private TextField minSizeField;
    private TextField maxSizeField;
    private TextField stepSizeField;
    
    private TextArea manualInputArea;
    private Label manualInputLabel;
    
    private VBox rangeInputBox;
    private HBox inputSizeBox;

    private Runnable onAnalyze;
    private Runnable onClear;

    public InputPane() {
        super(10);
        getStyleClass().add("left-pane");
        setPadding(new Insets(20));
        setPrefWidth(600);

        Label titleLabel = new Label("Code Input");
        titleLabel.getStyleClass().add("title-label");

        codeInputArea = new TextArea();
        codeInputArea.getStyleClass().add("code-input");
        codeInputArea.setWrapText(true);
        codeInputArea.setPrefRowCount(20);
        codeInputArea.setStyle("-fx-control-inner-background: #333333; -fx-text-fill: #ffffff; -fx-font-family: 'Consolas', monospace;");

        ToggleGroup inputTypeGroup = new ToggleGroup();
        singleInputRadio = new RadioButton("Single Input");
        rangeInputRadio = new RadioButton("Input Range");
        singleInputRadio.getStyleClass().add("radio-button");
        rangeInputRadio.getStyleClass().add("radio-button");
        singleInputRadio.setToggleGroup(inputTypeGroup);
        rangeInputRadio.setToggleGroup(inputTypeGroup);
        singleInputRadio.setSelected(true);

        HBox inputTypeBox = new HBox(10, singleInputRadio, rangeInputRadio);
        inputTypeBox.setAlignment(Pos.CENTER_LEFT);
        inputTypeBox.getStyleClass().add("input-section-box");

        manualInputRadio = new RadioButton("Manual Input");
        randomInputRadio = new RadioButton("Random Input");
        hardcodedInputRadio = new RadioButton("Hardcoded Input");
        manualInputRadio.getStyleClass().add("radio-button");
        randomInputRadio.getStyleClass().add("radio-button");
        hardcodedInputRadio.getStyleClass().add("radio-button");
        
        ToggleGroup singleInputGroup = new ToggleGroup();
        manualInputRadio.setToggleGroup(singleInputGroup);
        randomInputRadio.setToggleGroup(singleInputGroup);
        hardcodedInputRadio.setToggleGroup(singleInputGroup);
        randomInputRadio.setSelected(true);

        HBox singleInputBox = new HBox(10, manualInputRadio, randomInputRadio, hardcodedInputRadio);
        singleInputBox.setAlignment(Pos.CENTER_LEFT);
        singleInputBox.getStyleClass().add("input-section-box");

        HBox arrayTypeBox = new HBox(10);
        arrayTypeBox.setAlignment(Pos.CENTER_LEFT);
        Label arrayTypeLabel = new Label("Array Type:");
        arrayTypeLabel.setStyle("-fx-text-fill: #f0f0f0;");
        arrayTypeComboBox = new ComboBox<>();
        arrayTypeComboBox.getItems().addAll("Random", "Sorted", "Nearly Sorted");
        arrayTypeComboBox.setValue("Random");
        arrayTypeComboBox.setStyle("-fx-text-fill: black; -fx-background-color: white;");
        arrayTypeBox.getChildren().addAll(arrayTypeLabel, arrayTypeComboBox);

        inputSizeBox = new HBox(10);
        inputSizeBox.setAlignment(Pos.CENTER_LEFT);
        Label sizeLabel = new Label("Input Size:");
        sizeLabel.setStyle("-fx-text-fill: #f0f0f0;");
        inputSizeField = new TextField();
        inputSizeField.setPrefWidth(100);
        inputSizeField.setStyle("-fx-text-fill: black; -fx-background-color: white;");
        inputSizeBox.getChildren().addAll(sizeLabel, inputSizeField);

        Label minSizeLabel = new Label("Min Size:");
        minSizeLabel.getStyleClass().add("text-field-label");
        minSizeField = new TextField();
        minSizeField.setPrefWidth(100);
        minSizeField.getStyleClass().add("text-field");

        Label maxSizeLabel = new Label("Max Size:");
        maxSizeLabel.getStyleClass().add("text-field-label");
        maxSizeField = new TextField();
        maxSizeField.setPrefWidth(100);
        maxSizeField.getStyleClass().add("text-field");

        Label stepSizeLabel = new Label("Step Size:");
        stepSizeLabel.getStyleClass().add("text-field-label");
        stepSizeField = new TextField();
        stepSizeField.setPrefWidth(100);
        stepSizeField.getStyleClass().add("text-field");

        GridPane rangeInputGrid = new GridPane();
        rangeInputGrid.setHgap(10);
        rangeInputGrid.setVgap(5);
        rangeInputGrid.getStyleClass().add("range-input-grid");
        rangeInputGrid.add(minSizeLabel, 0, 0);
        rangeInputGrid.add(minSizeField, 1, 0);
        rangeInputGrid.add(maxSizeLabel, 2, 0);
        rangeInputGrid.add(maxSizeField, 3, 0);
        rangeInputGrid.add(stepSizeLabel, 0, 1);
        rangeInputGrid.add(stepSizeField, 1, 1);

        rangeInputBox = new VBox(5, rangeInputGrid);
        rangeInputBox.getStyleClass().add("input-section-box");
        rangeInputBox.setVisible(false);

        manualInputLabel = new Label("Manual Input Data:");
        manualInputLabel.getStyleClass().add("text-field-label");
        manualInputLabel.setStyle("-fx-text-fill: #f0f0f0;");
        manualInputArea = new TextArea();
        manualInputArea.setWrapText(true);
        manualInputArea.setPrefRowCount(5);
        manualInputArea.getStyleClass().add("manual-input-area");
        manualInputArea.setDisable(true);
        manualInputArea.setVisible(false);

        singleInputRadio.setOnAction(e -> toggleInputMode());
        rangeInputRadio.setOnAction(e -> toggleInputMode());
        manualInputRadio.setOnAction(e -> toggleInputFields());
        randomInputRadio.setOnAction(e -> toggleInputFields());
        hardcodedInputRadio.setOnAction(e -> toggleInputFields());

        Button analyzeButton = new Button("Analyze");
        analyzeButton.getStyleClass().add("analyze-button");
        analyzeButton.setOnAction(e -> { if (onAnalyze != null) onAnalyze.run(); });

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("clear-button");
        clearButton.setOnAction(e -> { if (onClear != null) onClear.run(); });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(analyzeButton, clearButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox userManualBox = new HBox();
        userManualBox.setAlignment(Pos.CENTER_RIGHT);
        Button userManualButton = new Button("User Manual");
        userManualButton.getStyleClass().add("user-manual-button");
        userManualButton.setOnAction(e -> showUserManual());
        userManualBox.getChildren().add(userManualButton);

        getChildren().addAll(
                titleLabel,
                codeInputArea,
                inputTypeBox,
                singleInputBox,
                arrayTypeBox,
                rangeInputBox,
                manualInputLabel,
                manualInputArea,
                inputSizeBox,
                buttonBox,
                spacer,
                userManualBox
        );

        toggleInputFields();
    }

    private void toggleInputFields() {
        if (manualInputRadio.isSelected()) {
            manualInputArea.setDisable(false);
            manualInputArea.setVisible(true);
            manualInputLabel.setVisible(true);
            inputSizeBox.setVisible(false);
        } else if (randomInputRadio.isSelected()) {
            manualInputArea.setDisable(true);
            manualInputArea.setVisible(false);
            manualInputLabel.setVisible(false);
            inputSizeBox.setVisible(true);
            inputSizeField.setDisable(false);
            inputSizeField.setStyle("-fx-text-fill: black; -fx-background-color: white;");
        } else { // Hardcoded input selected
            manualInputArea.setDisable(true);
            manualInputArea.setVisible(false);
            manualInputLabel.setVisible(false);
            inputSizeBox.setVisible(false);
        }
    }

    private void toggleInputMode() {
        if (singleInputRadio.isSelected()) {
            rangeInputBox.setVisible(false);
            manualInputRadio.setDisable(false);
            randomInputRadio.setDisable(false);
            hardcodedInputRadio.setDisable(false);
            toggleInputFields();
        } else if (rangeInputRadio.isSelected()) {
            rangeInputBox.setVisible(true);
            manualInputRadio.setDisable(true);
            randomInputRadio.setDisable(true);
            hardcodedInputRadio.setDisable(true);
            manualInputArea.setDisable(true);
            manualInputArea.setVisible(false);
            manualInputLabel.setVisible(false);
            inputSizeBox.setVisible(false);
        }
    }

    private void showUserManual() {
        UserManualUI userManual = new UserManualUI();
        userManual.show();
    }

    public void setOnAnalyze(Runnable onAnalyze) { this.onAnalyze = onAnalyze; }
    public void setOnClear(Runnable onClear) { this.onClear = onClear; }

    public String getCode() { return codeInputArea.getText(); }
    public void clearCode() { codeInputArea.clear(); }

    public boolean isRangeInput() { return rangeInputRadio.isSelected(); }
    public boolean isManualInput() { return manualInputRadio.isSelected(); }
    public boolean isRandomInput() { return randomInputRadio.isSelected(); }

    public String getManualInputText() { return manualInputArea.getText(); }
    public String getInputSizeText() { return inputSizeField.getText(); }
    public void clearInputSize() { inputSizeField.clear(); }

    public String getArrayType() { return arrayTypeComboBox.getValue(); }

    public String getMinSizeText() { return minSizeField.getText(); }
    public String getMaxSizeText() { return maxSizeField.getText(); }
    public String getStepSizeText() { return stepSizeField.getText(); }
}
