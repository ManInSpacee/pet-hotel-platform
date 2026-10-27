package ru.mirea.pethotel.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mirea.pethotel.backend.dto.OwnerResponse;
import ru.mirea.pethotel.backend.service.OwnerService;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
class OwnerController {
    private final OwnerService ownerService;

    OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public List<OwnerResponse> getAllOwners() {
        return ownerService.getAll().stream().map(OwnerResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OwnerResponse getOwnerById(@PathVariable Long id) {
        return OwnerResponse.from(ownerService.getById(id));
    }
}
