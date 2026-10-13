package com.hivda.couriertracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "T_STORE")
@Getter
@Setter
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "LATITUDE", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;
    @Column(name = "LONGITUDE", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    public Store(String name, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
