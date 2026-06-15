package main.ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CodeEditorPane extends StackPane {
    private TextArea codeInputArea;
    private TextArea lineNumbersArea;
    private Stage fullScreenStage;
    private boolean isFullScreen = false;
    private javafx.scene.layout.Pane originalParent;
    private int originalIndex;

    public CodeEditorPane() {
        this.getStyleClass().add("code-editor-container");

        codeInputArea = new TextArea();
        codeInputArea.setPromptText("// Paste your code here...");
        codeInputArea.getStyleClass().add("code-editor-text-area");
        codeInputArea.setWrapText(false);

        lineNumbersArea = new TextArea("1");
        lineNumbersArea.getStyleClass().add("code-editor-line-numbers");
        lineNumbersArea.setEditable(false);
        lineNumbersArea.setWrapText(false);
        lineNumbersArea.setPrefWidth(45);
        lineNumbersArea.setMinWidth(45);
        lineNumbersArea.setMaxWidth(45);

        // Update line numbers on text change
        codeInputArea.textProperty().addListener((obs, oldVal, newVal) -> updateLineNumbers());

        // Attempt to sync scroll bars when layout is complete
        Platform.runLater(this::bindScrollBars);
        codeInputArea.needsLayoutProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                bindScrollBars();
            }
        });

        HBox editorBox = new HBox(lineNumbersArea, codeInputArea);
        HBox.setHgrow(codeInputArea, Priority.ALWAYS);
        editorBox.getStyleClass().add("code-editor-hbox");

        Button fullScreenBtn = new Button();
        fullScreenBtn.getStyleClass().add("fullscreen-btn");
        
        // Fullscreen SVG Icon
        SVGPath icon = new SVGPath();
        icon.setContent("M3 3h6v2H5v4H3V3zm12 0h6v6h-2V5h-4V3zM3 21v-6h2v4h4v2H3zm16 0h-4v-2h4v-4h2v6h-2z");
        icon.setFill(Color.web("#8b949e"));
        fullScreenBtn.setGraphic(icon);
        fullScreenBtn.setOnAction(e -> toggleFullScreen());
        
        StackPane.setAlignment(fullScreenBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(fullScreenBtn, new Insets(10, 15, 0, 0));

        this.getChildren().addAll(editorBox, fullScreenBtn);
    }

    private void updateLineNumbers() {
        int lines = codeInputArea.getText().split("\n", -1).length;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) { 
            sb.append(i);
            if (i < lines) {
                sb.append("\n");
            }
        }
        lineNumbersArea.setText(sb.toString());
    }

    private void bindScrollBars() {
        javafx.scene.Node codeScrollBar = codeInputArea.lookup(".scroll-bar:vertical");
        javafx.scene.Node lineScrollBar = lineNumbersArea.lookup(".scroll-bar:vertical");
        javafx.scene.Node lineHorizontalScrollBar = lineNumbersArea.lookup(".scroll-bar:horizontal");

        if (codeScrollBar instanceof javafx.scene.control.ScrollBar && lineScrollBar instanceof javafx.scene.control.ScrollBar) {
            javafx.scene.control.ScrollBar csb = (javafx.scene.control.ScrollBar) codeScrollBar;
            javafx.scene.control.ScrollBar lsb = (javafx.scene.control.ScrollBar) lineScrollBar;
            
            if (!lsb.valueProperty().isBound()) {
                lsb.valueProperty().bindBidirectional(csb.valueProperty());
            }

            lineScrollBar.setOpacity(0);
            lineScrollBar.setDisable(true);
            lineScrollBar.setStyle("-fx-pref-width: 0; -fx-max-width: 0;");
        }
        
        if (lineHorizontalScrollBar != null) {
            lineHorizontalScrollBar.setOpacity(0);
            lineHorizontalScrollBar.setDisable(true);
            lineHorizontalScrollBar.setStyle("-fx-pref-height: 0; -fx-max-height: 0;");
        }
    }

    private void toggleFullScreen() {
        if (!isFullScreen) {
            originalParent = (javafx.scene.layout.Pane) this.getParent();
            originalIndex = originalParent.getChildren().indexOf(this);
            
            fullScreenStage = new Stage();
            fullScreenStage.initStyle(StageStyle.UNDECORATED);
            fullScreenStage.initModality(Modality.APPLICATION_MODAL);
            
            StackPane root = new StackPane(this);
            root.setStyle("-fx-background-color: #0b0f19; -fx-padding: 20px;");
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            fullScreenStage.setScene(scene);
            fullScreenStage.setFullScreenExitHint(""); // Remove the annoying ESC notification
            fullScreenStage.setFullScreen(true);
            
            // Handle ESC key exiting full screen natively
            fullScreenStage.fullScreenProperty().addListener((obs, wasFull, isNowFull) -> {
                if (!isNowFull && isFullScreen) {
                    toggleFullScreen();
                }
            });
            
            fullScreenStage.show();
            isFullScreen = true;
            
            SVGPath exitIcon = new SVGPath();
            exitIcon.setContent("M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z");
            exitIcon.setFill(Color.web("#8b949e"));
            ((Button) this.getChildren().get(1)).setGraphic(exitIcon);

        } else {
            if (fullScreenStage != null) {
                fullScreenStage.close();
            }
            if (originalParent != null) {
                if (!originalParent.getChildren().contains(this)) {
                    originalParent.getChildren().add(originalIndex, this);
                }
            }
            isFullScreen = false;
            
            SVGPath icon = new SVGPath();
            icon.setContent("M3 3h6v2H5v4H3V3zm12 0h6v6h-2V5h-4V3zM3 21v-6h2v4h4v2H3zm16 0h-4v-2h4v-4h2v6h-2z");
            icon.setFill(Color.web("#8b949e"));
            ((Button) this.getChildren().get(1)).setGraphic(icon);
        }
    }

    public TextArea getCodeInputArea() {
        return codeInputArea;
    }
}
