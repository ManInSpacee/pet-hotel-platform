package ru.mirea.pethotel.backend.domain;


import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Generated
    @Column(name = "code", nullable = false, insertable = false, updatable = false)
    private String code;

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

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "special_care", nullable = false)
    private boolean specialCare;

    @Column(name = "comment")
    private String comment;

    //    Колонка тегов из таблицы КОЛЛЕКЦИИ
    @ElementCollection
    @CollectionTable(name = "booking_tags", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "tag_name")
    private final Set<String> tags = new HashSet<>();

    @Column(name = "archived", nullable = false)
    private boolean archived;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Generated
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Booking(Pet pet,
                   Enclosure enclosure,
                   LocalDate startDate,
                   LocalDate endDate,
                   BigDecimal totalPrice,
                   boolean specialCare,
                   String comment) {
        this.pet = pet;
        this.enclosure = enclosure;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.specialCare = specialCare;
        this.comment = comment;
    }

    protected Booking() {
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

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Enclosure getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isSpecialCare() {
        return specialCare;
    }

    public void setSpecialCare(boolean specialCare) {
        this.specialCare = specialCare;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<String> getTags() {
        return tags;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
