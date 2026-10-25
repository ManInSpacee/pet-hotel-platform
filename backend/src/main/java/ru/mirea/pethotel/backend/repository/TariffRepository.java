package ru.mirea.pethotel.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.pethotel.backend.domain.EnclosureSize;
import ru.mirea.pethotel.backend.domain.Tariff;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    Optional<Tariff> findByEnclosureSize(EnclosureSize enclosureSize);
}
