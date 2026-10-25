package ru.mirea.pethotel.backend.service;


import org.springframework.stereotype.Service;
import ru.mirea.pethotel.backend.domain.*;
import ru.mirea.pethotel.backend.error.BusinessRuleException;
import ru.mirea.pethotel.backend.error.NotFoundException;
import ru.mirea.pethotel.backend.repository.BookingRepository;
import ru.mirea.pethotel.backend.repository.EnclosureRepository;
import ru.mirea.pethotel.backend.repository.PetRepository;
import ru.mirea.pethotel.backend.repository.TariffRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final EnclosureRepository enclosureRepo;
    private final PetRepository petRepo;
    private final TariffRepository tariffRepo;
    private static final int MIN_DAYS = 2;
    private static final int MAX_DAYS = 30;
    public static final List<BookingStatus> OCCUPYING_STATUSES = List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED);

    public BookingService(BookingRepository bookingRepo, EnclosureRepository enclosureRepo, PetRepository petRepo, TariffRepository tariffRepo) {
        this.bookingRepo = bookingRepo;
        this.enclosureRepo = enclosureRepo;
        this.petRepo = petRepo;
        this.tariffRepo = tariffRepo;
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new BusinessRuleException("Даты бронирования не могут быть пустыми");
        }
        if (end.isBefore(start)) {
            throw new BusinessRuleException("Дата окончания не может быть раньше даты начала");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Дата начала не может быть в прошлом");
        }
        if (ChronoUnit.DAYS.between(start, end) < MIN_DAYS) {
            throw new BusinessRuleException("Бронирование должно быть не менее " + MIN_DAYS + " дней");
        }
        if (ChronoUnit.DAYS.between(start, end) > MAX_DAYS) {
            throw new BusinessRuleException("Бронирование должно быть не более " + MAX_DAYS + " дней");
        }
    }


    public Booking accept(UUID bookingId) {
        return changeStatus(bookingId, BookingStatus.ACCEPTED);
    }

    public Booking deny(UUID bookingId) {
        return changeStatus(bookingId, BookingStatus.DENIED);
    }

    public Booking complete(UUID bookingId) {
        return changeStatus(bookingId, BookingStatus.COMPLETED);
    }

    public Booking cancel(UUID bookingId) {
        Booking booking = getById(bookingId);
        if (booking.getStatus() == BookingStatus.ACCEPTED && !LocalDate.now().isBefore(booking.getStartDate())) {
            throw new BusinessRuleException("Невозможно отменить бронирование, которое уже началось");
        }
        return changeStatus(bookingId, BookingStatus.CANCELLED);
    }

    public List<Enclosure> getAvailableEnclosures(Long petId, LocalDate start, LocalDate end) {

        validateDates(start, end);

        Pet pet = petRepo.findById(petId).orElseThrow(() -> new NotFoundException("Питомец с id " + petId + " не найден"));
        List<Enclosure> allEnclosures = enclosureRepo.findAll();
        return allEnclosures.stream()
                .filter(Enclosure::isActive)
                .filter(enc -> enc.canHost(pet.getSize()))
                .filter(enc -> !bookingRepo.hasOverlappingBookings(enc.getId(), OCCUPYING_STATUSES, start, end))
                .collect(Collectors.toList());
    }

    private Booking changeStatus(UUID bookingId, BookingStatus newStatus) {

        Booking booking = getById(bookingId);

        if (!booking.getStatus().canTransitionTo(newStatus)) {
            throw new BusinessRuleException("Невозможно изменить статус бронирования из " + booking.getStatus() + " в " + newStatus);
        }

        booking.setStatus(newStatus);

        return bookingRepo.save(booking);
    }

    public Booking create(Long petId, Long enclosureId, LocalDate start, LocalDate end, boolean specialCare, String comment) {

        validateDates(start, end);
        Pet pet = petRepo.findById(petId).orElseThrow(() -> new NotFoundException("Питомец с id " + petId + " не найден"));
        Enclosure enclosure = enclosureRepo.findById(enclosureId).orElseThrow(() -> new NotFoundException("Вольер с id " + enclosureId + " не найден"));

        if (!enclosure.isActive()) {
            throw new BusinessRuleException("Вольер с id: " + enclosure.getNumber() + " не активен.");
        }
        if (!enclosure.canHost(pet.getSize())) {
            throw new BusinessRuleException("Вольер не подходит для размера питомца");
        }
        if (bookingRepo.hasOverlappingBookings(enclosure.getId(), OCCUPYING_STATUSES, start, end)) {
            throw new BusinessRuleException("Вольер уже забронирован на указанный период");
        }

        Tariff tariff = tariffRepo.findByEnclosureSize(enclosure.getSize()).orElseThrow(() -> new NotFoundException("Тариф для размера " + enclosure.getSize() + " не найден"));
        long totalDays = ChronoUnit.DAYS.between(start, end);
        BigDecimal totalPrice = tariff.getPricePerDay()
                .multiply(BigDecimal.valueOf(totalDays));
        if (specialCare) {
            totalPrice = totalPrice.multiply(tariff.getSpecialCareCoefficient()).setScale(2, RoundingMode.HALF_UP);
        }

        return bookingRepo.save(new Booking(pet, enclosure, start, end, totalPrice, specialCare, comment));
    }

    public Booking getById(UUID bookingId) {
        return bookingRepo.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }

    public List<Booking> getAll() {
        return bookingRepo.findAll();
    }

    public void delete(UUID bookingId) {
        Booking booking = getById(bookingId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessRuleException("Невозможно удалить бронирование, которое не находится в состоянии PENDING");
        }
        bookingRepo.deleteById(bookingId);
    }

    public List<Booking> filterByStatus(BookingStatus status) {
        return bookingRepo.findByStatus(status);
    }

    public List<Booking> filterByDateRange(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new BusinessRuleException("Конец периода раньше начала");
        }
        return bookingRepo.findByDateRange(from, to);
    }

    public List<Booking> sortedByStartDate() {
        return bookingRepo.findAll().stream()
                .sorted(Comparator.comparing(Booking::getStartDate))
                .toList();
    }

    public List<Booking> sortedByCreatedAtNewestFirst() {
        return bookingRepo.findAll().stream()
                .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
                .toList();
    }
}
