package ru.mirea.pethotel.desktop.service;

import ru.mirea.pethotel.desktop.dto.BookingDto;

import java.time.LocalDate;
import java.util.function.Predicate;

public class BookingFilter {

    public Predicate<BookingDto> build(String search, String status, LocalDate from, LocalDate to) {
        String text = search == null ? "" : search.trim().toLowerCase();
        boolean anyStatus = status == null || status.equals("ANY");

        return b -> (b.code().toLowerCase().contains(text)
                    || b.petName().toLowerCase().contains(text)
                    || b.ownerName().toLowerCase().contains(text))
                && (anyStatus || b.status().equals(status))
                && (from == null || !b.endDate().isBefore(from))
                && (to == null || !b.startDate().isAfter(to));
    }
}
