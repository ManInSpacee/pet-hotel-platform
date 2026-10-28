package ru.mirea.pethotel.desktop.api;

import ru.mirea.pethotel.desktop.dto.BookingDto;
import ru.mirea.pethotel.desktop.dto.CreateBookingRequest;
import ru.mirea.pethotel.desktop.dto.EnclosureDto;
import ru.mirea.pethotel.desktop.dto.PetDto;
import ru.mirea.pethotel.desktop.dto.UpdateBookingRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeBookingApi implements BookingApi {

    private static final BigDecimal PRICE_PER_DAY = new BigDecimal("700.00");
    private static final BigDecimal SPECIAL_CARE_FACTOR = new BigDecimal("1.20");

    private final List<BookingDto> bookings = new ArrayList<>();
    private final List<PetDto> pets = new ArrayList<>();
    private final List<EnclosureDto> enclosures = new ArrayList<>();
    private int nextCodeNumber = 17;

    public FakeBookingApi() {
        pets.add(new PetDto(1L, "Анна Смирнова", "Рекс", "DOG", "LARGE"));
        pets.add(new PetDto(2L, "Анна Смирнова", "Бусинка", "CAT", "SMALL"));
        pets.add(new PetDto(3L, "Иван Петров", "Мурзик", "CAT", "SMALL"));
        pets.add(new PetDto(4L, "Мария Кузнецова", "Барсик", "CAT", "MEDIUM"));
        pets.add(new PetDto(5L, "Мария Кузнецова", "Луна", "DOG", "MEDIUM"));
        pets.add(new PetDto(6L, "Сергей Волков", "Шарик", "DOG", "LARGE"));
        pets.add(new PetDto(7L, "Елена Орлова", "Тоша", "DOG", "MEDIUM"));
        pets.add(new PetDto(8L, "Дмитрий Новиков", "Кеша", "BIRD", "SMALL"));
        pets.add(new PetDto(9L, "Ольга Белова", "Джесси", "DOG", "MEDIUM"));
        pets.add(new PetDto(10L, "Ольга Белова", "Пух", "RODENT", "SMALL"));

        String[] sizes = {"SMALL", "MEDIUM", "LARGE"};
        for (int number = 101; number <= 109; number++) {
            enclosures.add(new EnclosureDto((long) (number - 100), number, sizes[(number - 101) % 3], true));
        }

        add("BK-000001", "Рекс", "Анна Смирнова", 101, "2026-10-20", "2026-10-30", "ACCEPTED", "IN_HOTEL", "8400.00", true);
        add("BK-000002", "Мурзик", "Иван Петров", 102, "2026-10-25", "2026-11-02", "ACCEPTED", "IN_HOTEL", "5600.00", false);
        add("BK-000003", "Шарик", "Сергей Волков", 103, "2026-10-27", "2026-11-05", "ACCEPTED", "IN_HOTEL", "9200.00", true);
        add("BK-000004", "Барсик", "Мария Кузнецова", 104, "2026-11-03", "2026-11-10", "ACCEPTED", "UPCOMING", "4900.00", false);
        add("BK-000005", "Бусинка", "Анна Смирнова", 105, "2026-11-14", "2026-11-21", "ACCEPTED", "UPCOMING", "6720.00", true);
        add("BK-000006", "Тоша", "Елена Орлова", 106, "2026-11-20", "2026-11-27", "ACCEPTED", "UPCOMING", "7350.00", false);
        add("BK-000007", "Луна", "Мария Кузнецова", 101, "2026-12-20", "2027-01-05", "ACCEPTED", "UPCOMING", "15200.00", true);
        add("BK-000008", "Кеша", "Дмитрий Новиков", 107, "2026-11-01", "2026-11-08", "PENDING", "NOT_APPLICABLE", "2800.00", false);
        add("BK-000009", "Джесси", "Ольга Белова", 108, "2026-11-10", "2026-11-15", "PENDING", "NOT_APPLICABLE", "4100.00", true);
        add("BK-000010", "Пух", "Ольга Белова", 109, "2026-11-25", "2026-11-30", "PENDING", "NOT_APPLICABLE", "3500.00", false);
        add("BK-000011", "Рекс", "Анна Смирнова", 101, "2026-09-01", "2026-09-10", "COMPLETED", "FINISHED", "7560.00", false);
        add("BK-000012", "Мурзик", "Иван Петров", 102, "2026-09-15", "2026-09-20", "COMPLETED", "FINISHED", "3500.00", false);
        add("BK-000013", "Шарик", "Сергей Волков", 103, "2026-08-10", "2026-08-24", "COMPLETED", "FINISHED", "12600.00", true);
        add("BK-000014", "Барсик", "Мария Кузнецова", 104, "2026-10-05", "2026-10-12", "COMPLETED", "FINISHED", "1400.00", false);
        add("BK-000015", "Тоша", "Елена Орлова", 106, "2026-10-10", "2026-10-15", "DENIED", "NOT_APPLICABLE", "3000.00", false);
        add("BK-000016", "Кеша", "Дмитрий Новиков", 107, "2026-11-05", "2026-11-12", "CANCELLED", "NOT_APPLICABLE", "3150.00", true);
    }

    private void add(String code, String petName, String ownerName, int enclosureNumber,
                     String startDate, String endDate, String status, String stage,
                     String totalPrice, boolean specialCare) {
        PetDto pet = pets.stream()
                .filter(p -> p.name().equals(petName) && p.ownerName().equals(ownerName))
                .findFirst()
                .orElseThrow();
        bookings.add(new BookingDto(UUID.randomUUID(), code, pet.id(), petName, ownerName,
                (long) (enclosureNumber - 100), enclosureNumber,
                LocalDate.parse(startDate), LocalDate.parse(endDate), status, stage,
                new BigDecimal(totalPrice), specialCare, null, 0));
    }

    @Override
    public List<BookingDto> getAll() {
        return List.copyOf(bookings);
    }

    @Override
    public BookingDto create(CreateBookingRequest request) {
        PetDto pet = findPet(request.petId());
        EnclosureDto enclosure = findEnclosure(request.enclosureId());
        checkDates(request.startDate(), request.endDate());

        BookingDto created = new BookingDto(UUID.randomUUID(), String.format("BK-%06d", nextCodeNumber++),
                pet.id(), pet.name(), pet.ownerName(), enclosure.id(), enclosure.number(),
                request.startDate(), request.endDate(), "PENDING", "NOT_APPLICABLE",
                calculatePrice(request.startDate(), request.endDate(), request.specialCare()),
                request.specialCare(), request.comment(), 0);
        bookings.add(created);
        return created;
    }

    @Override
    public BookingDto update(UUID id, UpdateBookingRequest request) {
        int index = findBookingIndex(id);
        BookingDto old = bookings.get(index);

        if (!old.status().equals("PENDING") && !old.status().equals("ACCEPTED")) {
            throw new ApiException(409, "Изменить можно только бронь в статусе PENDING или ACCEPTED");
        }
        if (old.version() != request.version()) {
            throw new ApiException(409, "Бронь изменена кем-то другим, обновите список");
        }
        EnclosureDto enclosure = findEnclosure(request.enclosureId());
        checkDates(request.startDate(), request.endDate());

        BookingDto updated = new BookingDto(old.id(), old.code(), old.petId(), old.petName(), old.ownerName(),
                enclosure.id(), enclosure.number(), request.startDate(), request.endDate(),
                old.status(), old.stage(),
                calculatePrice(request.startDate(), request.endDate(), request.specialCare()),
                request.specialCare(), request.comment(), old.version() + 1);
        bookings.set(index, updated);
        return updated;
    }

    @Override
    public void delete(UUID id) {
        int index = findBookingIndex(id);
        if (!bookings.get(index).status().equals("PENDING")) {
            throw new ApiException(409, "Удалить можно только бронь в статусе PENDING");
        }
        bookings.remove(index);
    }

    @Override
    public List<PetDto> getPets() {
        return List.copyOf(pets);
    }

    @Override
    public List<EnclosureDto> getEnclosures() {
        return List.copyOf(enclosures);
    }

    private PetDto findPet(Long id) {
        return pets.stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "Питомец с id " + id + " не найден"));
    }

    private EnclosureDto findEnclosure(Long id) {
        return enclosures.stream()
                .filter(e -> e.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "Вольер с id " + id + " не найден"));
    }

    private int findBookingIndex(UUID id) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).id().equals(id)) {
                return i;
            }
        }
        throw new ApiException(404, "Бронь с id " + id + " не найдена");
    }

    private void checkDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new ApiException(400, "Ошибка валидации");
        }
        if (!end.isAfter(start)) {
            throw new ApiException(400, "Дата окончания должна быть позже даты начала");
        }
    }

    private BigDecimal calculatePrice(LocalDate start, LocalDate end, boolean specialCare) {
        BigDecimal price = PRICE_PER_DAY.multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(start, end)));
        if (specialCare) {
            price = price.multiply(SPECIAL_CARE_FACTOR);
        }
        return price.setScale(2);
    }
}
