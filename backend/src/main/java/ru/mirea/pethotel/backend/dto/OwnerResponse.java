package ru.mirea.pethotel.backend.dto;

import ru.mirea.pethotel.backend.domain.Owner;

public record OwnerResponse(
        Long id,
        String login,
        String fullName,
        String phone
) {
    public static OwnerResponse from(Owner o) {
        return new OwnerResponse(
                o.getId(),
                o.getLogin(),
                o.getFullName(),
                o.getPhone()
        );
    }
}
