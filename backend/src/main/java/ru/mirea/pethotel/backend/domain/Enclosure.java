package ru.mirea.pethotel.backend.domain;


import jakarta.persistence.*;

@Entity
@Table(name = "enclosures")
public class Enclosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false, unique = true)
    private int number;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private EnclosureSize size;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public Enclosure(int number, EnclosureSize size) {
        this.number = number;
        this.size = size;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSize(EnclosureSize size) {
        this.size = size;
    }

    protected Enclosure() {
    }

    @Override
    public String toString() {
        return "№: " + number + " (" + size + ")";
    }

    public boolean canHost(EnclosureSize petSize) {
        return this.size.compareTo(petSize) >= 0;
    }

    public int getNumber() {
        return number;
    }

    public EnclosureSize getSize() {
        return size;
    }

    public Long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
