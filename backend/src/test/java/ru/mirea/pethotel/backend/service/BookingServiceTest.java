package ru.mirea.pethotel.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.pethotel.backend.domain.Booking;
import ru.mirea.pethotel.backend.domain.BookingStatus;
import ru.mirea.pethotel.backend.error.BusinessRuleException;
import ru.mirea.pethotel.backend.error.NotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@Sql("/test-data.sql")
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Nested
    @DisplayName("Создание брони")
    class Create {

        @Test
        @DisplayName("создаёт бронь в статусе PENDING")
        void createBookingWithPendingStatus() {

            LocalDate start = LocalDate.now().plusDays(20);
            LocalDate end = LocalDate.now().plusDays(40);

            Booking booking = bookingService.create(2L, 3L, start, end);
            assertNotNull(booking.getId());
            assertEquals(BookingStatus.PENDING, booking.getStatus());
            assertEquals(start, booking.getStartDate());
        }


        @Test
        @DisplayName("отказывает если на выбранные даты уже создано бронирование")
        void cannotCreateBookingWithOverlappingDates() {

            LocalDate start = LocalDate.now().plusDays(3);
            LocalDate end = LocalDate.now().plusDays(20);

            BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> bookingService.create(2L, 3L, start, end));
            assertEquals("Вольер уже забронирован на указанный период", e.getMessage());
        }

        @Test
        @DisplayName("отказывает если размер питомца не соответствует размеру вольера")
        void cannotCreateBookingWithWrongEnclosureSize() {

            LocalDate start = LocalDate.now().plusDays(20);
            LocalDate end = LocalDate.now().plusDays(40);

            BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> bookingService.create(1L, 2L, start, end));
            assertEquals("Вольер не подходит для размера питомца", e.getMessage());
        }


        @Test
        @DisplayName("отказывает если дата бронирования указана в прошлом")
        void cannotCreateBookingInThePast() {

            LocalDate start = LocalDate.now().minusDays(20);
            LocalDate end = LocalDate.now().plusDays(1);

            BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> bookingService.create(2L, 3L, start, end));
            assertEquals("Дата начала не может быть в прошлом", e.getMessage());
        }

        @Test
        @DisplayName("отказывает если время бронирования менее двух дней")
        void cannotCreateBookingLessThanTwoDays() {

            LocalDate start = LocalDate.now().plusDays(20);
            LocalDate end = LocalDate.now().plusDays(21);

            BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> bookingService.create(2L, 3L, start, end));
            assertEquals("Бронирование должно быть не менее 2 дней", e.getMessage());
        }

        @Test
        @DisplayName("отказывает если время бронирования более 30 дней")
        void cannotCreateBookingWithMoreThanThirtyDays() {

            LocalDate start = LocalDate.now().plusDays(20);
            LocalDate end = LocalDate.now().plusDays(54);

            BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> bookingService.create(2L, 3L, start, end));
            assertEquals("Бронирование должно быть не более 30 дней", e.getMessage());
        }

        @Test
        @DisplayName("отказывает если даты бронирования не указаны")
        void cannotCreateBookingWithNullDates() {

            LocalDate start = null;
            LocalDate end = null;

            BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> bookingService.create(2L, 3L, start, end));
            assertEquals("Даты бронирования не могут быть пустыми", e.getMessage());
        }

        @Test
        @DisplayName("отказывает если дата окончания раньше даты начала")
        void cannotCreateBookingWithEndSoonerThanStart() {

            LocalDate start = LocalDate.now().plusDays(30);
            LocalDate end = LocalDate.now().plusDays(20);

            BusinessRuleException e = assertThrows(BusinessRuleException.class, () -> bookingService.create(2L, 3L, start, end));
            assertEquals("Дата окончания не может быть раньше даты начала", e.getMessage());
        }

        @Test
        @DisplayName("отказывает если указанный питомец не существует")
        void cannotCreateBookingWithNonexistentPet() {

            LocalDate start = LocalDate.now().plusDays(20);
            LocalDate end = LocalDate.now().plusDays(30);

            NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.create(20L, 3L, start, end));
            assertEquals("Питомец с id 20 не найден", e.getMessage());
        }

        @Test
        @DisplayName("отказывает если указанный вольер не существует")
        void cannotCreateBookingWithNonexistentEnclosure() {

            LocalDate start = LocalDate.now().plusDays(20);
            LocalDate end = LocalDate.now().plusDays(30);

            NotFoundException e = assertThrows(NotFoundException.class, () -> bookingService.create(2L, 30L, start, end));
            assertEquals("Вольер с id 30 не найден", e.getMessage());
        }
    }

}
