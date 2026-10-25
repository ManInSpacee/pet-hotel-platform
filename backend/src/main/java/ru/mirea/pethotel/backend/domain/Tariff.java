package ru.mirea.pethotel.backend.domain;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tariffs")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "enclosure_size", nullable = false)
    private EnclosureSize enclosureSize;

    @Column(name = "price_per_day", nullable = false)
    private BigDecimal pricePerDay;

    @Column(name = "special_care_coefficient", nullable = false)
    private BigDecimal specialCareCoefficient;


    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void setSpecialCareCoefficient(BigDecimal specialCareCoefficient) {
        this.specialCareCoefficient = specialCareCoefficient;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public Long getId() {
        return id;
    }

    public EnclosureSize getEnclosureSize() {
        return enclosureSize;
    }

    public BigDecimal getSpecialCareCoefficient() {
        return specialCareCoefficient;
    }


    protected Tariff() {
    }
}
