package com.hivda.couriertracking.service;

import com.hivda.couriertracking.entity.CourierLocation;
import com.hivda.couriertracking.entity.Store;
import com.hivda.couriertracking.entity.StoreEntrance;
import com.hivda.couriertracking.factory.StoreEntranceFactory;
import com.hivda.couriertracking.repository.StoreEntranceRepository;
import com.hivda.couriertracking.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreEntranceService {

    private static final double STORE_RADIUS_METERS = 100.0;
    private static final Duration REENTRY_INTERVAL = Duration.ofMinutes(1);

    private final StoreRepository storeRepository;
    private final StoreEntranceRepository entranceRepository;
    private final DistanceCalculator distanceCalculator;
    private final StoreEntranceFactory entranceFactory;

    public void checkStoreEntrances(CourierLocation currLocation, CourierLocation prevLocation) {
        List<Store> stores = storeRepository.findAll();

        for (Store store : stores) {
            double currentDistance = distanceCalculator.calculate(
                    currLocation.getLatitude(),
                    currLocation.getLongitude(),
                    store.getLatitude(),
                    store.getLongitude());

            boolean currentlyInside = currentDistance <= STORE_RADIUS_METERS;

            if (!currentlyInside) {
                continue;
            }

            boolean previouslyInside = isPreviouslyInside(prevLocation, store);

            if (previouslyInside) {
                continue;
            }

            saveEntrance(currLocation, store);
        }
    }

    private boolean isPreviouslyInside(CourierLocation prevLocation, Store store) {
        if (prevLocation == null) {
            return false;
        }
        double prevDistance = distanceCalculator.calculate(
                prevLocation.getLatitude(),
                prevLocation.getLongitude(),
                store.getLatitude(),
                store.getLongitude());
        return prevDistance <= STORE_RADIUS_METERS;
    }

    private void saveEntrance(CourierLocation location, Store store) {
        Optional<StoreEntrance> lastEntrance = entranceRepository.findTopByCourierIdAndStoreIdOrderByEntranceTimeDesc(
                location.getCourierId(), store.getId());

        // 1 dk kuralı
        if (lastEntrance.isPresent()) {
            Duration duration = Duration.between(
                    lastEntrance.get().getEntranceTime(),
                    location.getTimestamp());
            if (duration.compareTo(REENTRY_INTERVAL) < 0) {
                return;
            }
        }

        StoreEntrance entrance = entranceFactory.create(location, store);

        entranceRepository.save(entrance);
        log.info(
                "Courier {} entered store {} at {}",
                location.getCourierId(),
                store.getName(),
                location.getTimestamp());
    }

}
