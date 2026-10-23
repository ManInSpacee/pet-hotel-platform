package ru.mirea.pethotel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.pethotel.backend.domain.Pet;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwnerId(Long ownerId);
}
