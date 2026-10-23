package ru.mirea.pethotel.backend.domain;


import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false)
    private Species species;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private EnclosureSize size;

    public Pet(Owner owner, String name, Species species, EnclosureSize size) {
        this.owner = owner;
        this.name = name;
        this.species = species;
        this.size = size;
    }

    protected Pet() {
    }

    @Override
    public String toString() {
        return name + " - " + owner.getFullName() + " (" + size + ")";
    }

    public Long getId() {
        return id;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Species getSpecies() {
        return species;
    }

    public EnclosureSize getSize() {
        return size;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public void setSize(EnclosureSize size) {
        this.size = size;
    }
}
