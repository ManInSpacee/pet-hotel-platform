package ru.mirea.pethotel.desktop.api;

import ru.mirea.pethotel.desktop.dto.BookingDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FakeBookingApi implements BookingApi {

    private final List<BookingDto> bookings = new ArrayList<>();

    public FakeBookingApi() {
        // в гостинице сейчас
        add("BK-000001", "Рекс", "Анна Смирнова", 101, "2026-10-20", "2026-10-30", "ACCEPTED", "IN_HOTEL", "8400.00", true);
        add("BK-000002", "Мурзик", "Иван Петров", 102, "2026-10-25", "2026-11-02", "ACCEPTED", "IN_HOTEL", "5600.00", false);
        add("BK-000003", "Шарик", "Сергей Волков", 103, "2026-10-27", "2026-11-05", "ACCEPTED", "IN_HOTEL", "9200.00", true);
        // скоро заезд
        add("BK-000004", "Барсик", "Мария Кузнецова", 104, "2026-11-03", "2026-11-10", "ACCEPTED", "UPCOMING", "4900.00", false);
        add("BK-000005", "Бусинка", "Анна Смирнова", 105, "2026-11-14", "2026-11-21", "ACCEPTED", "UPCOMING", "6720.00", true);
        add("BK-000006", "Тоша", "Елена Орлова", 106, "2026-11-20", "2026-11-27", "ACCEPTED", "UPCOMING", "7350.00", false);
        add("BK-000007", "Луна", "Мария Кузнецова", 101, "2026-12-20", "2027-01-05", "ACCEPTED", "UPCOMING", "15200.00", true);
        // ждут подтверждения
        add("BK-000008", "Кеша", "Дмитрий Новиков", 107, "2026-11-01", "2026-11-08", "PENDING", "NOT_APPLICABLE", "2800.00", false);
        add("BK-000009", "Джесси", "Ольга Белова", 108, "2026-11-10", "2026-11-15", "PENDING", "NOT_APPLICABLE", "4100.00", true);
        add("BK-000010", "Пух", "Ольга Белова", 109, "2026-11-25", "2026-11-30", "PENDING", "NOT_APPLICABLE", "3500.00", false);
        // завершённые
        add("BK-000011", "Рекс", "Анна Смирнова", 101, "2026-09-01", "2026-09-10", "COMPLETED", "FINISHED", "7560.00", false);
        add("BK-000012", "Мурзик", "Иван Петров", 102, "2026-09-15", "2026-09-20", "COMPLETED", "FINISHED", "3500.00", false);
        add("BK-000013", "Шарик", "Сергей Волков", 103, "2026-08-10", "2026-08-24", "COMPLETED", "FINISHED", "12600.00", true);
        add("BK-000014", "Барсик", "Мария Кузнецова", 104, "2026-10-05", "2026-10-12", "COMPLETED", "FINISHED", "1400.00", false);
        // отклонённые и отменённые
        add("BK-000015", "Тоша", "Елена Орлова", 106, "2026-10-10", "2026-10-15", "DENIED", "NOT_APPLICABLE", "3000.00", false);
        add("BK-000016", "Кеша", "Дмитрий Новиков", 107, "2026-11-05", "2026-11-12", "CANCELLED", "NOT_APPLICABLE", "3150.00", true);
    }

    private void add(String code, String petName, String ownerName, int enclosureNumber,
                     String startDate, String endDate, String status, String stage,
                     String totalPrice, boolean specialCare) {
        bookings.add(new BookingDto(UUID.randomUUID(), code, petName, ownerName, enclosureNumber,
                LocalDate.parse(startDate), LocalDate.parse(endDate), status, stage,
                new BigDecimal(totalPrice), specialCare));
    }

    @Override
    public List<BookingDto> getAll() {
        return List.copyOf(bookings);
    }
}
