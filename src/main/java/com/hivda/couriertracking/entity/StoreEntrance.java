package com.hivda.couriertracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "T_STORE_ENTRANCE")
@Getter
@Setter
@NoArgsConstructor
public class StoreEntrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "COURIER_ID", nullable = false)
    private String courierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @Column(name = "ENTRANCE_TIME", nullable = false)
    private Instant entranceTime;

    public StoreEntrance(String courierId, Store store, Instant entranceTime) {
        this.courierId = courierId;
        this.store = store;
        this.entranceTime = entranceTime;
    }

}
