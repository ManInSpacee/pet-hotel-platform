package ru.mirea.pethotel.backend.domain;

import java.time.LocalDate;

public enum BookingStage {
    NOT_APPLICABLE, UPCOMING, IN_HOTEL, FINISHED;

    public static BookingStage calculateStage(BookingStatus bookingStatus, LocalDate today, LocalDate start, LocalDate end) {

        if (bookingStatus == BookingStatus.PENDING || bookingStatus == BookingStatus.DENIED || bookingStatus == BookingStatus.CANCELLED) {
            return NOT_APPLICABLE;
        }
        if (bookingStatus == BookingStatus.COMPLETED) {
            return FINISHED;
        }
        if (today.isAfter(end)) {
            return FINISHED;
        }
        if (today.isBefore(start)) {
            return UPCOMING;
        }
        return IN_HOTEL;
    }
}


