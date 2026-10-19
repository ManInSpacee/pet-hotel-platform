package ru.mirea.pethotel.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.mirea.pethotel.PetHotelDesktopApplication;

import java.io.IOException;

public class PetHotelFxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = SpringApplication.run(PetHotelDesktopApplication.class);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        loader.setControllerFactory(context::getBean);
        stage.setTitle("Pet Hotel");
        stage.setScene(new Scene(loader.load(), 1100, 700));
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
    }
}
