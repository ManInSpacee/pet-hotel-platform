 package ru.mirea.pethotel.core.service;


import org.springframework.stereotype.Service;
import ru.mirea.pethotel.core.entity.Booking;
import ru.mirea.pethotel.core.entity.BookingStatus;
import ru.mirea.pethotel.core.entity.Enclosure;
import ru.mirea.pethotel.core.entity.Pet;
import ru.mirea.pethotel.core.exception.BusinessRuleException;
import ru.mirea.pethotel.core.exception.NotFoundException;
import ru.mirea.pethotel.core.repository.BookingRepository;
import ru.mirea.pethotel.core.repository.EnclosureRepository;
import ru.mirea.pethotel.core.repository.PetRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final EnclosureRepository enclosureRepo;
    private final PetRepository petRepo;
    private static final int MIN_DAYS = 2;
    private static final int MAX_DAYS = 30;
    public static final List<BookingStatus> OCCUPYING_STATUSES = List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED);

    public BookingService(BookingRepository bookingRepo, EnclosureRepository enclosureRepo, PetRepository petRepo) {
        this.bookingRepo = bookingRepo;
        this.enclosureRepo = enclosureRepo;
        this.petRepo = petRepo;
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


    public Booking accept(Long bookingId) {
        return changeStatus(bookingId, BookingStatus.ACCEPTED);
    }
    public Booking deny(Long bookingId) {
        return changeStatus(bookingId, BookingStatus.DENIED);
    }

    public Booking complete(Long bookingId) {
        return changeStatus(bookingId, BookingStatus.COMPLETED);
    }

    public Booking cancel(Long bookingId) {
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
                .filter(enc -> enc.canHost(pet.getSize()))
                .filter(enc -> !bookingRepo.hasOverlappingBookings(enc.getId(), OCCUPYING_STATUSES, start, end))
                .collect(Collectors.toList());
    }

    private Booking changeStatus(Long bookingId, BookingStatus newStatus) {

        Booking booking = getById(bookingId);

        if (!booking.getStatus().canTransitionTo(newStatus)) {
            throw new BusinessRuleException("Невозможно изменить статус бронирования из " + booking.getStatus() + " в " + newStatus);
        }

        booking.setStatus(newStatus);

        return bookingRepo.save(booking);
    }

    public Booking create(Long petId, Long enclosureId, LocalDate start, LocalDate end) {

        validateDates(start, end);
        Pet pet = petRepo.findById(petId).orElseThrow(() -> new NotFoundException("Питомец с id " + petId + " не найден"));
        Enclosure enclosure = enclosureRepo.findById(enclosureId).orElseThrow(() -> new NotFoundException("Вольер с id " + enclosureId + " не найден"));

        if (!enclosure.canHost(pet.getSize())) {
            throw new BusinessRuleException("Вольер не подходит для размера питомца");
        }
        if (bookingRepo.hasOverlappingBookings(enclosure.getId(), OCCUPYING_STATUSES, start, end)) {
            throw new BusinessRuleException("Вольер уже забронирован на указанный период");
        }

        return bookingRepo.save(new Booking (pet, enclosure, start, end));
    }

    public Booking getById(Long bookingId) {
        return bookingRepo.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));
    }

    public List<Booking> getAll() {
        return bookingRepo.findAll();
    }

    public void delete(Long bookingId) {
        getById(bookingId);
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
