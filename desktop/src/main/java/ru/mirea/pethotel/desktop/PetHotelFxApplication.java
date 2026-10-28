package ru.mirea.pethotel.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.mirea.pethotel.desktop.api.BookingApi;
import ru.mirea.pethotel.desktop.api.FakeBookingApi;
import ru.mirea.pethotel.desktop.api.HttpBookingApi;
import ru.mirea.pethotel.desktop.controller.MainController;

import java.io.IOException;

public class PetHotelFxApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        BookingApi api = createApi();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        loader.setControllerFactory(type -> new MainController(api));
        stage.setTitle("Pet Hotel");
        stage.setScene(new Scene(loader.load(), 1100, 700));
        stage.show();
    }

    // По умолчанию данные из памяти; с backend: запуск с -Dapi=http
    private static BookingApi createApi() {
        if ("http".equals(System.getProperty("api"))) {
            return new HttpBookingApi();
        }
        return new FakeBookingApi();
    }
}
