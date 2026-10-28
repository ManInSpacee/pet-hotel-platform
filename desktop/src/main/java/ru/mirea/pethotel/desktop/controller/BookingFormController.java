package ru.mirea.pethotel.desktop.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ru.mirea.pethotel.desktop.api.ApiException;
import ru.mirea.pethotel.desktop.api.BookingApi;
import ru.mirea.pethotel.desktop.dto.BookingDto;
import ru.mirea.pethotel.desktop.dto.CreateBookingRequest;
import ru.mirea.pethotel.desktop.dto.EnclosureDto;
import ru.mirea.pethotel.desktop.dto.PetDto;
import ru.mirea.pethotel.desktop.dto.UpdateBookingRequest;
import ru.mirea.pethotel.desktop.util.Dialogs;

import java.time.LocalDate;

public class BookingFormController {

    private final BookingApi bookingApi;

    private BookingDto editing;

    @FXML
    private ComboBox<PetDto> petComboBox;

    @FXML
    private ComboBox<EnclosureDto> enclosureComboBox;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private CheckBox specialCareCheckBox;
    @FXML
    private TextArea commentArea;

    public BookingFormController(BookingApi bookingApi) {
        this.bookingApi = bookingApi;
    }

    @FXML
    public void close() {
        Stage stage = (Stage) petComboBox.getScene().getWindow();
        stage.close();
    }

    public void setBooking(BookingDto booking) {
        this.editing = booking;
        petComboBox.getItems().stream()
                .filter(p -> p.id().equals(booking.petId()))
                .findFirst()
                .ifPresent(petComboBox::setValue);
        petComboBox.setDisable(true);
        enclosureComboBox.getItems().stream()
                .filter(e -> e.id().equals(booking.enclosureId()))
                .findFirst()
                .ifPresent(enclosureComboBox::setValue);
        startDatePicker.setValue(booking.startDate());
        endDatePicker.setValue(booking.endDate());
        specialCareCheckBox.setSelected(booking.specialCare());
        commentArea.setText(booking.comment() == null ? "" : booking.comment());
    }

    @FXML
    private void onSaveClick() {
        PetDto pet = petComboBox.getValue();
        if (pet == null) {
            Dialogs.showError("Питомец не выбран");
            return;
        }
        EnclosureDto enclosure = enclosureComboBox.getValue();
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
        boolean specialCare = specialCareCheckBox.isSelected();
        String comment = commentArea.getText().isBlank() ? null : commentArea.getText();

        try {
            if (editing == null) {
                bookingApi.create(new CreateBookingRequest(pet.id(), enclosure.id(), startDate, endDate, specialCare, comment));
            } else {
                bookingApi.update(editing.id(),
                        new UpdateBookingRequest(enclosure.id(), startDate, endDate, specialCare, comment, editing.version()));
            }
            close();
        } catch (ApiException e) {
            Dialogs.showError(e);
        }
    }

    @FXML
    private void initialize() {
        petComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PetDto pet) {
                return pet == null ? "" : pet.name() + " (" + pet.ownerName() + ")";
            }

            @Override
            public PetDto fromString(String text) {
                return null;
            }
        });
        enclosureComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(EnclosureDto enclosure) {
                return enclosure == null ? "" : "№" + enclosure.number() + " (" + enclosure.size() + ")";
            }

            @Override
            public EnclosureDto fromString(String text) {
                return null;
            }
        });
        try {
            petComboBox.setItems(FXCollections.observableArrayList(bookingApi.getPets()));
            enclosureComboBox.setItems(FXCollections.observableArrayList(bookingApi.getEnclosures()));
        } catch (ApiException e) {
            Dialogs.showError(e);
        }
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
}
