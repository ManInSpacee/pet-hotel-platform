package ru.mirea.pethotel.desktop.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookingDto(UUID id, String code, String petName, String ownerName, int enclosureNumber, LocalDate startDate,
                         LocalDate endDate, String status, String stage, BigDecimal totalPrice, boolean specialCare) {}
