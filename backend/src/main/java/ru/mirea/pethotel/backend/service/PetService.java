package ru.mirea.pethotel.backend.service;


import org.springframework.stereotype.Service;
import ru.mirea.pethotel.backend.domain.EnclosureSize;
import ru.mirea.pethotel.backend.domain.Owner;
import ru.mirea.pethotel.backend.domain.Pet;
import ru.mirea.pethotel.backend.domain.Species;
import ru.mirea.pethotel.backend.error.BusinessRuleException;
import ru.mirea.pethotel.backend.error.NotFoundException;
import ru.mirea.pethotel.backend.repository.BookingRepository;
import ru.mirea.pethotel.backend.repository.OwnerRepository;
import ru.mirea.pethotel.backend.repository.PetRepository;

import java.util.List;

@Service
public class PetService {
    private final PetRepository petRepo;
    private final OwnerRepository ownerRepo;
    private final BookingRepository bookingRepo;

    public PetService(PetRepository petRepo, OwnerRepository ownerRepo, BookingRepository bookingRepo) {
        this.petRepo = petRepo;
        this.ownerRepo = ownerRepo;
        this.bookingRepo = bookingRepo;
    }

    private void validatePetData(Long ownerId, String name, Species species, EnclosureSize size) {
        if (ownerId == null) {
            throw new BusinessRuleException("ID владельца не может быть пустым");
        }
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("Имя питомца не может быть пустым");
        }
        if (name.length() > 20) {
            throw new BusinessRuleException("Имя питомца не может быть длиннее 20 символов");
        }
        if (!name.matches("\\p{L}+")) {
            throw new BusinessRuleException("Имя питомца должно содержать только буквы");
        }
        if (species == null) {
            throw new BusinessRuleException("Вид питомца не может быть пустым");
        }
        if (size == null) {
            throw new BusinessRuleException("Размер питомца не может быть пустым");
        }
    }

    public Pet getById(Long petId) {
        return petRepo.findById(petId).orElseThrow(() -> new NotFoundException("Питомец с id " + petId + " не найден"));
    }

    public List<Pet> getByOwnerId(Long ownerId) {
        findOwner(ownerId);
        return petRepo.findByOwnerId(ownerId);
    }


    private Owner findOwner(Long ownerId) {
        return ownerRepo.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владелец не найден"));
    }

    public Pet create(Long ownerId, String name, Species species, EnclosureSize size) {
        validatePetData(ownerId, name, species, size);
        Owner owner = findOwner(ownerId);
        Pet pet = new Pet(owner, name, species, size);
        return petRepo.save(pet);
    }

    public List<Pet> getAll() {
        return petRepo.findAll();
    }

    public List<Pet> searchByName(String part) {
        String lower = part.toLowerCase();
        return petRepo.findAll().stream()
                .filter(pet -> pet.getName().toLowerCase().contains(lower))
                .toList();
    }

    public Pet update(Long petId, Long ownerId, String newName, Species newSpecies, EnclosureSize newSize) {
        validatePetData(ownerId, newName, newSpecies, newSize);
        Owner owner = findOwner(ownerId);
        Pet pet = getById(petId);

        pet.setOwner(owner);
        pet.setName(newName);
        pet.setSpecies(newSpecies);
        pet.setSize(newSize);

        return petRepo.save(pet);
    }

    public void delete(Long petId) {
        getById(petId);
        if (bookingRepo.existsByPetId(petId)) {
            throw new BusinessRuleException("Невозможно удалить питомца с существующими бронированиями");
        }
        petRepo.deleteById(petId);
    }
}

