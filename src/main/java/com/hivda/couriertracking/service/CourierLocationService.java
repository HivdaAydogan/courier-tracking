package com.hivda.couriertracking.service;

import com.hivda.couriertracking.dto.CourierLocationRequest;
import com.hivda.couriertracking.entity.CourierLocation;
import com.hivda.couriertracking.mapper.CourierTrackingMapper;
import com.hivda.couriertracking.repository.CourierLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourierLocationService {

    private final CourierLocationRepository repository;
    private final CourierTrackingMapper mapper;
    private final StoreEntranceService entranceService;
    private final DistanceCalculator distanceCalculator;

    @Transactional
    public void processLocation(CourierLocationRequest request) {
        CourierLocation prevLocation = repository
                .findTopByCourierIdOrderByTimestampDesc(request.getCourierId()).orElse(null);

        CourierLocation currLocation = mapper.toEntity(request);

        repository.save(currLocation);

        entranceService.checkStoreEntrances(currLocation, prevLocation);
    }

    @Transactional(readOnly = true)
    public double getTotalTravelDistance(String courierId) {
        List<CourierLocation> locations = repository.findByCourierIdOrderByTimestampAsc(courierId);

        double totalDistance = 0.0;

        for (int i = 1; i < locations.size(); i++) {
            CourierLocation prev = locations.get(i - 1);
            CourierLocation curr = locations.get(i);

            totalDistance += distanceCalculator.calculate(
                    prev.getLatitude(),
                    prev.getLongitude(),
                    curr.getLatitude(),
                    curr.getLongitude());
        }

        return totalDistance;
    }

}
