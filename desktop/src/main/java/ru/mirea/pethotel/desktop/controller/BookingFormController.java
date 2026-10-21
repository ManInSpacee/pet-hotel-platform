package ru.mirea.pethotel.desktop.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.mirea.pethotel.core.entity.Enclosure;
import ru.mirea.pethotel.core.entity.Pet;
import ru.mirea.pethotel.core.service.BookingService;
import ru.mirea.pethotel.core.service.EnclosureService;
import ru.mirea.pethotel.core.service.PetService;


@Component
@Scope("prototype")
public class BookingFormController {

    private final PetService petService;
    private final EnclosureService enclosureService;
    private final BookingService bookingService;

    @FXML
    private ComboBox<Pet> petComboBox;

    @FXML
    private ComboBox<Enclosure> enclosureComboBox;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    public BookingFormController(PetService petService, EnclosureService enclosureService, BookingService bookingService) {
        this.petService = petService;
        this.enclosureService = enclosureService;
        this.bookingService = bookingService;
    }

    @FXML
    public void close() {
        Stage stage = (Stage) petComboBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void initialize() {
        petComboBox.setItems(FXCollections.observableArrayList(petService.getAll()));
        enclosureComboBox.setItems(FXCollections.observableArrayList(enclosureService.getAll()));
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
}
