package ru.mirea.pethotel.backend.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingStageTest {

    private final LocalDate start = LocalDate.of(2026, 11, 1);
    private final LocalDate end = LocalDate.of(2026, 11, 10);
    private final LocalDate today = LocalDate.of(2026, 11, 6);

    @Test
    @DisplayName("PENDING в день заезда -> NOT_APPLICABLE")
    void changeStageToNotApplicable() {
        assertEquals(BookingStage.NOT_APPLICABLE, BookingStage.calculateStage(BookingStatus.PENDING, today, start, end));
    }

    @Test
    @DisplayName("COMPLETED -> FINISHED")
    void changeStageToFinished() {
        assertEquals(BookingStage.FINISHED, BookingStage.calculateStage(BookingStatus.COMPLETED, today, start, end));
        assertEquals(BookingStage.FINISHED, BookingStage.calculateStage(BookingStatus.ACCEPTED, today.plusDays(20), start, end));
    }

    @Test
    @DisplayName("ACCEPTED до дня заезда -> UPCOMING")
    void changeStageToUpcoming() {
        assertEquals(BookingStage.UPCOMING, BookingStage.calculateStage(BookingStatus.ACCEPTED, today.minusDays(20), start, end));
    }

    @Test
    @DisplayName("ACCEPTED в середине бронирования -> IN_HOTEL")
    void changeStageToInHotelInMiddle() {
        assertEquals(BookingStage.IN_HOTEL, BookingStage.calculateStage(BookingStatus.ACCEPTED, today, start, end));
    }

    @Test
    @DisplayName("ACCEPTED в день заезда -> IN_HOTEL")
    void changeStageToInHotel() {
        assertEquals(BookingStage.IN_HOTEL, BookingStage.calculateStage(BookingStatus.ACCEPTED, start, start, end));
    }

    @Test
    @DisplayName("ACCEPTED в день выезда -> IN_HOTEL")
    void changeStageToInHotelOnCheckoutDay() {
        assertEquals(BookingStage.IN_HOTEL, BookingStage.calculateStage(BookingStatus.ACCEPTED, end, start, end));
    }
}
