package ru.mirea.pethotel.core.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.Generated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "enclosure_id", nullable = false)
    private Enclosure enclosure;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Generated
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Booking(Pet pet, Enclosure enclosure, LocalDate startDate, LocalDate endDate) {
        this.pet = pet;
        this.enclosure = enclosure;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Booking() {
    }

    @Override
    public String toString() {
        return "[ ID: " + id + ", питомец: " + pet.getId() + ", вольер: " + enclosure.getId()
                + ", " + startDate + " → " + endDate + ", " + status + "]";
    }

    public long durationDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public boolean isActiveOn(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public Long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public Enclosure getEnclosure() {
        return enclosure;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
