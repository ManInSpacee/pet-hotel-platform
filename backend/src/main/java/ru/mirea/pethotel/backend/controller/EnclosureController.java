package ru.mirea.pethotel.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mirea.pethotel.backend.dto.EnclosureResponse;
import ru.mirea.pethotel.backend.service.EnclosureService;

import java.util.List;

@RestController
@RequestMapping("/api/enclosures")
class EnclosureController {
    private final EnclosureService enclosureService;

    EnclosureController(EnclosureService enclosureService) {
        this.enclosureService = enclosureService;
    }

    @GetMapping
    public List<EnclosureResponse> getAllEnclosures() {
        return enclosureService.getAll().stream().map(EnclosureResponse::from).toList();
    }

    @GetMapping("/{id}")
    public EnclosureResponse getEnclosureById(@PathVariable Long id) {
        return EnclosureResponse.from(enclosureService.getById(id));
    }
}
