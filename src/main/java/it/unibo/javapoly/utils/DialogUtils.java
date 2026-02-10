package it.unibo.javapoly.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class DialogUtils {
    public static void showMessage(String title, String message){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        dialogStage.setResizable(true);

        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getBounds().getWidth();
        double screenHeight = screen.getBounds().getHeight();

        dialogStage.setWidth(screenWidth * 0.5);
        dialogStage.setHeight(screenHeight * 0.3);

        alert.getButtonTypes().setAll(ButtonType.OK);

        alert.showAndWait();
    }
}
