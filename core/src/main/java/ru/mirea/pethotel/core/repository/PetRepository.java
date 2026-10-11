package ru.mirea.pethotel.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.pethotel.core.entity.Pet;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwnerId(Long ownerId);
}
