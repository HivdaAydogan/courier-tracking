package com.hivda.couriertracking.factory;

import com.hivda.couriertracking.entity.CourierLocation;
import com.hivda.couriertracking.entity.Store;
import com.hivda.couriertracking.entity.StoreEntrance;
import org.springframework.stereotype.Component;

@Component
public class StoreEntranceFactory {

    public StoreEntrance create(
            CourierLocation location,
            Store store) {

        return new StoreEntrance(
                location.getCourierId(),
                store,
                location.getTimestamp()
        );
    }

}
