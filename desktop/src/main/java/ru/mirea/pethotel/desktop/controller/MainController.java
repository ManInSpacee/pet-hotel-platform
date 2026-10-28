package ru.mirea.pethotel.desktop.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mirea.pethotel.desktop.PetHotelFxApplication;
import ru.mirea.pethotel.desktop.api.ApiException;
import ru.mirea.pethotel.desktop.api.BookingApi;
import ru.mirea.pethotel.desktop.dto.BookingDto;
import ru.mirea.pethotel.desktop.service.BookingFilter;
import ru.mirea.pethotel.desktop.service.CsvExporter;
import ru.mirea.pethotel.desktop.util.Dialogs;
import ru.mirea.pethotel.desktop.util.Styles;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class MainController {

    private final BookingApi bookingApi;
    @FXML
    private TableView<BookingDto> bookingTable;
    @FXML
    private TableColumn<BookingDto, String> codeColumn;
    @FXML
    private TableColumn<BookingDto, String> petColumn;
    @FXML
    private TableColumn<BookingDto, String> ownerColumn;
    @FXML
    private TableColumn<BookingDto, Integer> enclosureColumn;
    @FXML
    private TableColumn<BookingDto, LocalDate> startDateColumn;
    @FXML
    private TableColumn<BookingDto, LocalDate> endDateColumn;
    @FXML
    private TableColumn<BookingDto, String> statusColumn;
    @FXML
    private TableColumn<BookingDto, String> stageColumn;
    @FXML
    private TableColumn<BookingDto, BigDecimal> priceColumn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private DatePicker fromPicker;
    @FXML
    private DatePicker toPicker;
    @FXML
    private StatisticsController statisticsController;

    private final ObservableList<BookingDto> bookings = FXCollections.observableArrayList();
    private final BookingFilter filter = new BookingFilter();
    private final CsvExporter exporter = new CsvExporter();
    private final FilteredList<BookingDto> filtered = new FilteredList<>(bookings, b -> true);
    private final SortedList<BookingDto> sorted = new SortedList<>(filtered);

    public MainController(BookingApi bookingApi) {
        this.bookingApi = bookingApi;
    }

    private void refreshBookings() {
        try {
            bookings.setAll(bookingApi.getAll());
            statisticsController.update(bookings);
        } catch (ApiException e) {
            Dialogs.showError(e);
        }
    }

    private void setupColumns() {
        codeColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().code()));
        petColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().petName()));
        ownerColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().ownerName()));
        enclosureColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().enclosureNumber()));
        startDateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().startDate()));
        endDateColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().endDate()));
        statusColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().status()));
        stageColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().stage()));
        priceColumn.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().totalPrice()));
    }

    @FXML
    private void initialize() {
        setupColumns();
        sorted.comparatorProperty().bind(bookingTable.comparatorProperty());
        bookingTable.setItems(sorted);
        statusComboBox.getItems().addAll("ANY", "PENDING", "ACCEPTED", "DENIED", "CANCELLED", "COMPLETED");
        statusComboBox.setValue("ANY");

        searchField.textProperty().addListener((obs, oldText, newText) -> applyFilter());
        statusComboBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilter());
        fromPicker.valueProperty().addListener((obs, oldValue, newValue) -> applyFilter());
        toPicker.valueProperty().addListener((obs, oldValue, newValue) -> applyFilter());

        refreshBookings();
    }

    @FXML
    public void onRefreshClick() {
        refreshBookings();
    }

    @FXML
    public void onExportClick() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Экспорт в CSV");
        chooser.setInitialFileName("bookings.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = chooser.showSaveDialog(bookingTable.getScene().getWindow());
        if (file == null) {
            return;
        }
        exportTo(file);
    }

    private void exportTo(File file) {
        try {
            exporter.export(sorted, file.toPath());
        } catch (IOException e) {
            Dialogs.showError("Не удалось сохранить файл: " + e.getMessage());
        }
    }

    @FXML
    public void onDeleteClick() {
        BookingDto selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.showError("Выберите бронирование для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Удалить бронирование " + selected.code() + " (" + selected.petName() + ")?");
        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.filter(b -> b == ButtonType.OK).isPresent()) {
            try {
                bookingApi.delete(selected.id());
                refreshBookings();
            } catch (ApiException e) {
                Dialogs.showError(e);
            }
        }
    }

    @FXML
    public void onAddClick() throws IOException {
        openForm(null);
    }

    @FXML
    public void onEditClick() throws IOException {
        BookingDto selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.showError("Выберите бронирование для изменения");
            return;
        }
        openForm(selected);
    }

    private void openForm(BookingDto booking) throws IOException {
        FXMLLoader loader = new FXMLLoader(PetHotelFxApplication.class.getResource("booking-form.fxml"));
        loader.setControllerFactory(type -> new BookingFormController(bookingApi));
        Parent root = loader.load();
        BookingFormController form = loader.getController();
        if (booking != null) {
            form.setBooking(booking);
        }
        Stage stage = new Stage();
        stage.setTitle(booking == null ? "Создание бронирования" : "Изменение бронирования " + booking.code());
        Scene scene = new Scene(root);
        Styles.apply(scene);
        stage.setScene(scene);
        Styles.applyIcon(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        refreshBookings();
    }

    private void applyFilter() {
        filtered.setPredicate(filter.build(searchField.getText(), statusComboBox.getValue(), fromPicker.getValue(),
                toPicker.getValue()));
    }
}
