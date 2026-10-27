package ru.mirea.pethotel.backend.dto;

import ru.mirea.pethotel.backend.domain.EnclosureSize;
import ru.mirea.pethotel.backend.domain.Pet;
import ru.mirea.pethotel.backend.domain.Species;

public record PetResponse(
        Long id,
        String ownerName,
        String name,
        Species species,
        EnclosureSize size
) {
    public static PetResponse from(Pet p) {
        return new PetResponse(
                p.getId(),
                p.getOwner().getFullName(),
                p.getName(),
                p.getSpecies(),
                p.getSize()
        );
    }
}

