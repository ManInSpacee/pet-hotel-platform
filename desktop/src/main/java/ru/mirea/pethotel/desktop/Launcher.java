package ru.mirea.pethotel.desktop;

import javafx.application.Application;

// Отдельный main, который не наследуется от Application: иначе при запуске из jar
// падает «JavaFX runtime components are missing»
public class Launcher {

    public static void main(String[] args) {
        Application.launch(PetHotelFxApplication.class, args);
    }
}
