package ru.mirea.pethotel.desktop.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.mirea.pethotel.core.entity.Booking;
import ru.mirea.pethotel.core.entity.BookingStatus;
import ru.mirea.pethotel.core.service.BookingService;

import java.time.LocalDate;

@Component
@Scope("prototype")
public class MainController {

    private final BookingService bookingService;
    @FXML
    private TableView<Booking> bookingTable;
    @FXML
    private TableColumn<Booking, String> petColumn;
    @FXML
    private TableColumn<Booking, String> ownerColumn;
    @FXML
    private TableColumn<Booking, Integer> enclosureColumn;
    @FXML
    private TableColumn<Booking, LocalDate> startDateColumn;
    @FXML
    private TableColumn<Booking, LocalDate> endDateColumn;
    @FXML
    private TableColumn<Booking, BookingStatus> statusColumn;

    private final ObservableList<Booking> bookings = FXCollections.observableArrayList();

    private void refreshBookings() {
        bookings.setAll(bookingService.getAll());
    }

    private void setupColumns() {
        petColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPet().getName()));
        ownerColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getPet().getOwner().getFullName()));
        enclosureColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getEnclosure().getNumber()));
        startDateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getEndDate()));
        statusColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getStatus()));
    }

    public MainController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @FXML
    private void initialize() {
        setupColumns();
        bookingTable.setItems(bookings);
        refreshBookings();
    }
}
