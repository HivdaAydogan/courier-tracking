package com.hivda.couriertracking.repository;

import com.hivda.couriertracking.entity.CourierLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourierLocationRepository extends JpaRepository<CourierLocation, Long> {

    List<CourierLocation> findByCourierIdOrderByTimestampAsc(String courierId);

    Optional<CourierLocation> findTopByCourierIdOrderByTimestampDesc(String courierId);

}
