package ru.mirea.pethotel.desktop.util;

import javafx.scene.control.Alert;
import ru.mirea.pethotel.desktop.api.ApiException;

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

    public static void showError(ApiException e) {
        StringBuilder text = new StringBuilder(e.getMessage());
        e.getFields().forEach((field, problem) -> text.append("\n").append(field).append(": ").append(problem));
        showError(text.toString());
    }
}
