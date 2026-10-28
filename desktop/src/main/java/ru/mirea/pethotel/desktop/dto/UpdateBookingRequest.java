package ru.mirea.pethotel.desktop.dto;

import java.time.LocalDate;

public record UpdateBookingRequest(Long enclosureId, LocalDate startDate, LocalDate endDate,
                                   boolean specialCare, String comment, long version) {}
