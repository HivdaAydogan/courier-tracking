package com.hivda.couriertracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "T_COURIER_LOCATION")
@Getter
@Setter
@NoArgsConstructor
public class CourierLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "COURIER_ID", nullable = false)
    private String courierId;
    @Column(name = "LOCATION_TIME", nullable = false)
    private Instant timestamp;
    @Column(name = "LATITUDE", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;
    @Column(name = "LONGITUDE", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    public CourierLocation(
            String courierId,
            Instant timestamp,
            BigDecimal latitude,
            BigDecimal longitude) {
        this.courierId = courierId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
