package ru.mirea.pethotel.desktop.service;

import ru.mirea.pethotel.desktop.api.FakeBookingApi;
import ru.mirea.pethotel.desktop.dto.BookingDto;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        List<BookingDto> a = new FakeBookingApi().getAll();
        BookingStatistics testCount = new BookingStatistics();
        System.out.println(testCount.countBookings(a));
        System.out.println(testCount.countBookingsStream(a));

    }
}
