package ru.mirea.pethotel.desktop.util;

import javafx.scene.Scene;
import javafx.scene.control.DialogPane;

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
}
