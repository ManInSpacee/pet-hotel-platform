package ru.mirea.pethotel;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.mirea.pethotel.desktop.PetHotelFxApplication;

// Лежит в корневом пакете, чтобы Spring сканировал и ru.mirea.pethotel.backend, и ru.mirea.pethotel.desktop
@SpringBootApplication
public class PetHotelDesktopApplication {

    public static void main(String[] args) {
        Application.launch(PetHotelFxApplication.class, args);
    }
}
