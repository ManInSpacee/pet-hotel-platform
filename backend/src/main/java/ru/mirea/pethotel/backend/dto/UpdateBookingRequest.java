package ru.mirea.pethotel.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateBookingRequest(
        @NotNull Long enclosureId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        Boolean specialCare,
        @Size(max = 500) String comment,
        @NotNull Long version
) {
    public UpdateBookingRequest {
        if (specialCare == null) {
            specialCare = false;
        }
    }
}
