package ru.mirea.pethotel.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.pethotel.core.entity.Booking;
import ru.mirea.pethotel.core.entity.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPetOwnerId(Long ownerId);
    @Query("""
        select count(b) > 0
        from Booking b
        where b.enclosure.id = :enclosureId
          and b.status in :statuses
          and b.endDate >= :start
          and b.startDate <= :end
        """)
    boolean hasOverlappingBookings(
            @Param("enclosureId") Long enclosureId,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);


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
