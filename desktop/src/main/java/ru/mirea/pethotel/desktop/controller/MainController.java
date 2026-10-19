package ru.mirea.pethotel.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.mirea.pethotel.core.service.BookingService;

@Component
@Scope("prototype")
public class MainController {

    private final BookingService bookingService;

    @FXML
    private Label statusLabel;

    public MainController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @FXML
    private void initialize() {
        statusLabel.setText("Бронирований в базе: " + bookingService.getAll().size());
    }
}
