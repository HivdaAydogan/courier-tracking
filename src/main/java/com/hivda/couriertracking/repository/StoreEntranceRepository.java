package com.hivda.couriertracking.repository;

import com.hivda.couriertracking.entity.StoreEntrance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreEntranceRepository extends JpaRepository<StoreEntrance, Long> {

    // Kuryenin store'a yaptığı son giriş
    Optional<StoreEntrance> findTopByCourierIdAndStoreIdOrderByEntranceTimeDesc(
            String courierId, Long storeId);

}
