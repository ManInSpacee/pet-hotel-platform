package ru.mirea.pethotel.backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateBookingRequest(
        @NotNull Long petId,
        @NotNull Long enclosureId,
        @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull LocalDate endDate,
        Boolean specialCare,
        @Size(max = 500) String comment
) {
    public CreateBookingRequest {
        if (specialCare == null) {
            specialCare = false;
        }
    }
}
