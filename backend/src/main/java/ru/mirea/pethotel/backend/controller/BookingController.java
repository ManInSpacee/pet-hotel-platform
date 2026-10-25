package ru.mirea.pethotel.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mirea.pethotel.backend.domain.Booking;
import ru.mirea.pethotel.backend.dto.BookingResponse;
import ru.mirea.pethotel.backend.dto.CreateBookingRequest;
import ru.mirea.pethotel.backend.service.BookingService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping()
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAll().stream().map(BookingResponse::from).toList();
    }

    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable UUID id) {
        return BookingResponse.from(bookingService.getById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.create(
                request.petId(),
                request.enclosureId(),
                request.startDate(),
                request.endDate(),
                request.specialCare(),
                request.comment());
        return BookingResponse.from(booking);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable UUID id) {
        bookingService.delete(id);
    }
}