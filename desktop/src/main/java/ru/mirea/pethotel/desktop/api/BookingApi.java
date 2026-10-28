package ru.mirea.pethotel.desktop.api;

import ru.mirea.pethotel.desktop.dto.BookingDto;
import ru.mirea.pethotel.desktop.dto.CreateBookingRequest;
import ru.mirea.pethotel.desktop.dto.EnclosureDto;
import ru.mirea.pethotel.desktop.dto.PetDto;
import ru.mirea.pethotel.desktop.dto.UpdateBookingRequest;

import java.util.List;
import java.util.UUID;

public interface BookingApi {

    List<BookingDto> getAll();                                  // GET    /api/bookings

    BookingDto create(CreateBookingRequest request);            // POST   /api/bookings

    BookingDto update(UUID id, UpdateBookingRequest request);   // PUT    /api/bookings/{id}

    void delete(UUID id);                                       // DELETE /api/bookings/{id}

    List<PetDto> getPets();                                     // GET    /api/pets

    List<EnclosureDto> getEnclosures();                         // GET    /api/enclosures
}
