package ru.mirea.pethotel.backend.dto;

import ru.mirea.pethotel.backend.domain.Enclosure;
import ru.mirea.pethotel.backend.domain.EnclosureSize;

public record EnclosureResponse(
        Long id,
        int number,
        EnclosureSize size,
        boolean active
) {
    public static EnclosureResponse from(Enclosure e) {
        return new EnclosureResponse(
                e.getId(),
                e.getNumber(),
                e.getSize(),
                e.isActive()
        );
    }
}
