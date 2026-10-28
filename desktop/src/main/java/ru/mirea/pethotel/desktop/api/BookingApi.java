package ru.mirea.pethotel.desktop.api;

import ru.mirea.pethotel.desktop.dto.BookingDto;

import java.util.List;

public interface BookingApi {

    List<BookingDto> getAll();
}
