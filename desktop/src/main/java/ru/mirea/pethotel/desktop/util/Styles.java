package ru.mirea.pethotel.desktop.util;

import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public final class Styles {

    private static final String APP_CSS = Styles.class.getResource("/ru/mirea/pethotel/desktop/app.css").toExternalForm();

    private Styles() {
    }

    public static void apply(Scene scene) {
        scene.getStylesheets().add(APP_CSS);
    }

    public static void apply(DialogPane pane) {
        pane.getStylesheets().add(APP_CSS);
    }

    // Несколько размеров: Windows сама берёт подходящий для заголовка окна и панели задач
    public static void applyIcon(Stage stage) {
        for (int size : new int[]{16, 32, 64, 128, 256}) {
            stage.getIcons().add(new Image(Styles.class.getResourceAsStream("/ru/mirea/pethotel/desktop/icon-" + size + ".png")));
        }
    }
}
