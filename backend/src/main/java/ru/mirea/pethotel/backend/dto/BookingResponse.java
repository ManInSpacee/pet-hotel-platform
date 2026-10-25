package ru.mirea.pethotel.backend.dto;

import ru.mirea.pethotel.backend.domain.Booking;
import ru.mirea.pethotel.backend.domain.BookingStage;
import ru.mirea.pethotel.backend.domain.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        String code,
        Long petId,
        Long enclosureId,
        int enclosureNumber,
        String petName,
        String ownerName,
        LocalDate startDate,
        LocalDate endDate,
        BookingStatus status,
        BookingStage stage,
        BigDecimal totalPrice,
        boolean specialCare,
        String comment,
        Set<String> tags,
        boolean archived,
        LocalDateTime archivedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long version
) {
    public static BookingResponse from(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getCode(),
                b.getPet().getId(),
                b.getEnclosure().getId(),
                b.getEnclosure().getNumber(),
                b.getPet().getName(),
                b.getPet().getOwner().getFullName(),
                b.getStartDate(),
                b.getEndDate(),
                b.getStatus(),
                BookingStage.calculateStage(b.getStatus(), LocalDate.now(), b.getStartDate(), b.getEndDate()),
                b.getTotalPrice(),
                b.isSpecialCare(),
                b.getComment(),
                Set.copyOf(b.getTags()),
                b.isArchived(),
                b.getArchivedAt(),
                b.getCreatedAt(),
                b.getUpdatedAt(),
                b.getVersion()
        );
    }
}
