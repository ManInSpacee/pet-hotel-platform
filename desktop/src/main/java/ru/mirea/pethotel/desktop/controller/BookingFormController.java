package ru.mirea.pethotel.desktop.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.mirea.pethotel.backend.domain.Enclosure;
import ru.mirea.pethotel.backend.domain.Pet;
import ru.mirea.pethotel.backend.error.BusinessRuleException;
import ru.mirea.pethotel.backend.service.BookingService;
import ru.mirea.pethotel.backend.service.EnclosureService;
import ru.mirea.pethotel.backend.service.PetService;
import ru.mirea.pethotel.desktop.util.Dialogs;

import java.time.LocalDate;


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
    private void onSaveClick() {
        Pet pet = petComboBox.getValue();
        if (pet == null) {
            Dialogs.showError("Питомец не выбран");
            return;
        }
        Enclosure enclosure = enclosureComboBox.getValue();
        if (enclosure == null) {
            Dialogs.showError("Вольер не выбран");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        if (startDate == null) {
            Dialogs.showError("Дата начала не выбрана");
            return;
        }
        LocalDate endDate = endDatePicker.getValue();
        if (endDate == null) {
            Dialogs.showError("Дата окончания не выбрана");
            return;
        }
        try {
            bookingService.create(pet.getId(), enclosure.getId(), startDate, endDate, false, null);
            close();
        } catch (BusinessRuleException e) {
            Dialogs.showError("Ошибка при создании бронирования: " + e.getMessage());
        }


    }

    @FXML
    private void initialize() {
        petComboBox.setItems(FXCollections.observableArrayList(petService.getAll()));
        enclosureComboBox.setItems(FXCollections.observableArrayList(enclosureService.getAll()));
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
}
