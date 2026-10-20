package ru.mirea.pethotel.desktop.util;

import javafx.scene.control.Alert;

public final class Dialogs {
    private Dialogs() {
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
