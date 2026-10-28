package ru.mirea.pethotel.desktop.dto;

import java.time.LocalDate;

// Полное новое состояние брони; version — та, что пришла в BookingDto при загрузке
public record UpdateBookingRequest(Long enclosureId, LocalDate startDate, LocalDate endDate,
                                   boolean specialCare, String comment, long version) {}
