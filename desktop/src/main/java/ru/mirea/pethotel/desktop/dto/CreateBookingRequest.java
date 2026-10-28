package ru.mirea.pethotel.desktop.dto;

import java.time.LocalDate;

public record CreateBookingRequest(Long petId, Long enclosureId, LocalDate startDate, LocalDate endDate,
                                   boolean specialCare, String comment) {}
