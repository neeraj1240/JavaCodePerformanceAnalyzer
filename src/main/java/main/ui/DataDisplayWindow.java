package main.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DataDisplayWindow {
    private Stage stage;
    private String windowTitle;

    public DataDisplayWindow(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public void showData(String data) {
        if (data == null || data.isEmpty()) {
            UIUtils.showAlert("No Data", "No data available for " + windowTitle + ". Please run the analysis first.");
            return;
        }

        if (stage == null) {
            stage = new Stage();
            stage.setTitle(windowTitle);
            stage.initModality(Modality.NONE);
            UIUtils.setStageIcon(stage);
        }

        VBox content = new VBox(10);
        content.getStyleClass().add("data-display");
        content.setPadding(new Insets(20));

        Label titleLabel = new Label(windowTitle);
        titleLabel.getStyleClass().add("title-label");
        titleLabel.setStyle("-fx-text-fill: #f0f0f0;");

        TextArea dataArea = new TextArea(data);
        dataArea.setEditable(false);
        dataArea.setWrapText(true);
        dataArea.setPrefRowCount(20);
        dataArea.getStyleClass().add("data-area");
        dataArea.setStyle("-fx-control-inner-background: #2b2b2b; -fx-text-fill: #ffffff; -fx-font-family: 'Consolas', monospace; -fx-font-size: 13px;");

        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction(e -> stage.close());

        content.getChildren().addAll(titleLabel, dataArea, closeButton);

        Scene scene = new Scene(content, 400, 500);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load styles.css: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.show();
    }
}
