package ru.mirea.pethotel.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.pethotel.core.entity.Enclosure;

import java.util.Optional;

public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {
    Optional<Enclosure> findByNumber(int number);
}
