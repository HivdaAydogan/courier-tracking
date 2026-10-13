package com.hivda.couriertracking;

import com.hivda.couriertracking.entity.CourierLocation;
import com.hivda.couriertracking.entity.Store;
import com.hivda.couriertracking.entity.StoreEntrance;
import com.hivda.couriertracking.factory.StoreEntranceFactory;
import com.hivda.couriertracking.repository.StoreEntranceRepository;
import com.hivda.couriertracking.repository.StoreRepository;
import com.hivda.couriertracking.service.DistanceCalculator;
import com.hivda.couriertracking.service.DistanceCalculatorImpl;
import com.hivda.couriertracking.service.StoreEntranceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreEntranceServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreEntranceRepository entranceRepository;

    private StoreEntranceService storeEntranceService;

    private Store store;

    private StoreEntranceFactory entranceFactory;

    @BeforeEach
    void setUp() {

        DistanceCalculator distanceCalculator =
                new DistanceCalculatorImpl();

        entranceFactory = new StoreEntranceFactory();

        storeEntranceService = new StoreEntranceService(
                storeRepository,
                entranceRepository,
                distanceCalculator,
                entranceFactory
        );

        store = new Store(
                "Ataşehir MMM Migros",
                new BigDecimal("40.9923307"),
                new BigDecimal("29.1244229")
        );

        store.setId(1L);

        when(storeRepository.findAll())
                .thenReturn(List.of(store));
    }

    @Test
    void shouldSaveEntranceWhenCourierEntersStoreRadius() {

        CourierLocation previousLocation =
                createOutsideLocation(
                        "2026-10-06T15:00:00Z"
                );

        CourierLocation currentLocation =
                createInsideLocation(
                        "2026-10-06T15:00:20Z"
                );

        when(entranceRepository
                .findTopByCourierIdAndStoreIdOrderByEntranceTimeDesc(
                        "C100",
                        1L
                ))
                .thenReturn(Optional.empty());

        storeEntranceService.checkStoreEntrances(
                currentLocation,
                previousLocation
        );

        verify(entranceRepository)
                .save(any(StoreEntrance.class));
    }

    @Test
    void shouldNotSaveEntranceWhenCourierWasAlreadyInside() {

        CourierLocation previousLocation =
                createInsideLocation(
                        "2026-10-06T15:00:00Z"
                );

        CourierLocation currentLocation =
                createInsideLocation(
                        "2026-10-06T15:00:20Z"
                );

        storeEntranceService.checkStoreEntrances(
                currentLocation,
                previousLocation
        );

        verify(entranceRepository, never())
                .save(any(StoreEntrance.class));

        verify(entranceRepository, never())
                .findTopByCourierIdAndStoreIdOrderByEntranceTimeDesc(
                        anyString(),
                        anyLong()
                );
    }

    @Test
    void shouldNotSaveEntranceWhenReentryIsWithinOneMinute() {

        /*
         * First entrance: 15:00:20
         *
         * Courier later leaves the radius.
         * Current location at 15:00:50 is another outside -> inside
         * transition.
         *
         * Only 30 seconds passed, therefore it must not be saved.
         */

        CourierLocation previousLocation =
                createOutsideLocation(
                        "2026-10-06T15:00:40Z"
                );

        CourierLocation currentLocation =
                createInsideLocation(
                        "2026-10-06T15:00:50Z"
                );

        StoreEntrance lastEntrance = new StoreEntrance(
                "C100",
                store,
                Instant.parse("2026-10-06T15:00:20Z")
        );

        when(entranceRepository
                .findTopByCourierIdAndStoreIdOrderByEntranceTimeDesc(
                        "C100",
                        1L
                ))
                .thenReturn(Optional.of(lastEntrance));

        storeEntranceService.checkStoreEntrances(
                currentLocation,
                previousLocation
        );

        verify(entranceRepository, never())
                .save(any(StoreEntrance.class));
    }

    @Test
    void shouldSaveEntranceWhenReentryIsAfterOneMinute() {

        /*
         * First entrance: 15:00:20
         * Re-entry:       15:01:30
         *
         * 70 seconds passed, therefore a new entrance is allowed.
         */

        CourierLocation previousLocation =
                createOutsideLocation(
                        "2026-10-06T15:01:20Z"
                );

        CourierLocation currentLocation =
                createInsideLocation(
                        "2026-10-06T15:01:30Z"
                );

        StoreEntrance lastEntrance = new StoreEntrance(
                "C100",
                store,
                Instant.parse("2026-10-06T15:00:20Z")
        );

        when(entranceRepository
                .findTopByCourierIdAndStoreIdOrderByEntranceTimeDesc(
                        "C100",
                        1L
                ))
                .thenReturn(Optional.of(lastEntrance));

        storeEntranceService.checkStoreEntrances(
                currentLocation,
                previousLocation
        );

        verify(entranceRepository)
                .save(any(StoreEntrance.class));
    }

    private CourierLocation createInsideLocation(String timestamp) {

        // Exactly at the store location -> distance = 0
        return new CourierLocation(
                "C100",
                Instant.parse(timestamp),
                new BigDecimal("40.9923307"),
                new BigDecimal("29.1244229")
        );
    }

    private CourierLocation createOutsideLocation(String timestamp) {

        // Approximately 450 meters away from the store
        return new CourierLocation(
                "C100",
                Instant.parse(timestamp),
                new BigDecimal("40.9900000"),
                new BigDecimal("29.1200000")
        );
    }
}
