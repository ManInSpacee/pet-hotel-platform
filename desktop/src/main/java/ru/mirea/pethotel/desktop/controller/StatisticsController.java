package ru.mirea.pethotel.desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ru.mirea.pethotel.desktop.dto.BookingDto;
import ru.mirea.pethotel.desktop.service.BookingStatistics;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatisticsController {

    private final BookingStatistics statistics = new BookingStatistics();

    @FXML
    private Label totalLabel;
    @FXML
    private Label inHotelLabel;
    @FXML
    private Label meanLabel;
    @FXML
    private Label minLabel;
    @FXML
    private Label maxLabel;
    @FXML
    private Label specialCareLabel;
    @FXML
    private GridPane statusGrid;

    public void update(List<BookingDto> bookings) {
        totalLabel.setText(String.valueOf(statistics.countBookingsStream(bookings)));
        inHotelLabel.setText(String.valueOf(statistics.countBookingsNowStream(bookings)));
        meanLabel.setText(statistics.meanCostStream(bookings).toPlainString() + " ₽");
        minLabel.setText(statistics.minCostStream(bookings).toPlainString() + " ₽");
        maxLabel.setText(statistics.maxCostStream(bookings).toPlainString() + " ₽");
        specialCareLabel.setText(String.format("%.2f%%",statistics.specialCareRateStream(bookings)));

        statusGrid.getChildren().clear();
        Map<String, Long> byStatus = new TreeMap<>(statistics.countBookingsByStatusStream(bookings));
        int row = 0;
        for (Map.Entry<String, Long> entry : byStatus.entrySet()) {
            Label status = new Label(entry.getKey());
            Label count = new Label(String.valueOf(entry.getValue()));
            count.getStyleClass().add("value");
            statusGrid.add(status, 0, row);
            statusGrid.add(count, 1, row);
            row++;
        }
    }
}
