package ru.mirea.pethotel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.pethotel.backend.domain.Booking;
import ru.mirea.pethotel.backend.domain.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByPetOwnerId(Long ownerId);

    @Query("""
            select count(b) > 0
            from Booking b
            where b.enclosure.id = :enclosureId
              and b.status in :statuses
              and b.endDate >= :start
              and b.startDate <= :end
              and (:excludeId is null or b.id <> :excludeId)
            """)
    boolean hasOverlappingBookings(
            @Param("enclosureId") Long enclosureId,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("excludeId") UUID excludeId);


    @Query("""
            select b
            from Booking b
            where b.endDate >= :from
            and b.startDate <= :to
            """)
    List<Booking> findByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    List<Booking> findByStatus(BookingStatus status);

    boolean existsByEnclosureId(Long enclosureId);

    boolean existsByPetId(Long petId);

}
