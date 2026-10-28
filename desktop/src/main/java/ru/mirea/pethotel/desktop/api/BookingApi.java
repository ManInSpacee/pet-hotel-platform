package ru.mirea.pethotel.desktop.api;

import ru.mirea.pethotel.desktop.dto.BookingDto;
import ru.mirea.pethotel.desktop.dto.CreateBookingRequest;
import ru.mirea.pethotel.desktop.dto.EnclosureDto;
import ru.mirea.pethotel.desktop.dto.PetDto;
import ru.mirea.pethotel.desktop.dto.UpdateBookingRequest;

import java.util.List;
import java.util.UUID;

public interface BookingApi {

    List<BookingDto> getAll();

    BookingDto create(CreateBookingRequest request);

    BookingDto update(UUID id, UpdateBookingRequest request);

    void delete(UUID id);

    List<PetDto> getPets();

    List<EnclosureDto> getEnclosures();
}
