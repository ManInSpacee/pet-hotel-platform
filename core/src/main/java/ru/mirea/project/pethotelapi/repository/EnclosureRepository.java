package ru.mirea.project.pethotelapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.project.pethotelapi.entity.Enclosure;

import java.util.Optional;

public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {
    Optional<Enclosure> findByNumber(int number);
}
