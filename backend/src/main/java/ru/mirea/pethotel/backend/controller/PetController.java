package ru.mirea.pethotel.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mirea.pethotel.backend.dto.PetResponse;
import ru.mirea.pethotel.backend.service.PetService;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
class PetController {
    private final PetService petService;

    PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public List<PetResponse> getAllPets() {
        return petService.getAll().stream().map(PetResponse::from).toList();
    }

    @GetMapping("/{id}")
    public PetResponse getPetById(@PathVariable Long id) {
        return PetResponse.from(petService.getById(id));
    }

}
