package ru.mirea.pethotel.desktop.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.mirea.pethotel.core.entity.Booking;
import ru.mirea.pethotel.core.entity.BookingStatus;
import ru.mirea.pethotel.core.service.BookingService;
import ru.mirea.pethotel.desktop.PetHotelFxApplication;

import java.io.IOException;
import java.time.LocalDate;

@Component
@Scope("prototype")
public class MainController {

    private final BookingService bookingService;
    private final ApplicationContext context;
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


    public MainController(BookingService bookingService, ApplicationContext context) {
        this.bookingService = bookingService;
        this.context = context;
    }

    @FXML
    private void initialize() {
        setupColumns();
        bookingTable.setItems(bookings);
        refreshBookings();
    }

    @FXML
    public void onAddClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(PetHotelFxApplication.class.getResource("booking-form.fxml"));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Создание бронирования");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        refreshBookings();
//        Booking booking = form.getResult();
//        if (updated != null) {
//            int index = movies.indexOf(selected);
//            movies.set(index, updated);
//        }
    }
}
