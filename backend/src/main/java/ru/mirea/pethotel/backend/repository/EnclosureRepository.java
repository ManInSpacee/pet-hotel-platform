package ru.mirea.pethotel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.pethotel.backend.domain.Enclosure;

import java.util.Optional;

public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {
    Optional<Enclosure> findByNumber(int number);
}
