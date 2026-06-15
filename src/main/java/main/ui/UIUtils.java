package main.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UIUtils {
    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void setIcon(Labeled labeled, String resourcePath, double size) {
        try {
            var stream = UIUtils.class.getResourceAsStream(resourcePath);
            if (stream != null) {
                Image icon = new Image(stream);
                ImageView imageView = new ImageView(icon);
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
                imageView.setPreserveRatio(true);
                labeled.setGraphic(imageView);
                labeled.setGraphicTextGap(10);
            } else {
                System.err.println("Icon not found at: " + resourcePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon from " + resourcePath + ": " + e.getMessage());
        }
    }
}
