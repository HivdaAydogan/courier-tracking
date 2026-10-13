package com.hivda.couriertracking;

import com.hivda.couriertracking.service.DistanceCalculator;
import com.hivda.couriertracking.service.DistanceCalculatorImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceCalculatorImplTest {

    private final DistanceCalculator distanceCalculator =
            new DistanceCalculatorImpl();

    @Test
    void shouldReturnZeroWhenLocationsAreSame() {

        BigDecimal latitude = new BigDecimal("40.9923307");
        BigDecimal longitude = new BigDecimal("29.1244229");

        double distance = distanceCalculator.calculate(
                latitude,
                longitude,
                latitude,
                longitude
        );

        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void shouldCalculateDistanceBetweenTwoLocations() {

        BigDecimal lat1 = new BigDecimal("40.9900000");
        BigDecimal lng1 = new BigDecimal("29.1200000");

        BigDecimal lat2 = new BigDecimal("40.9923307");
        BigDecimal lng2 = new BigDecimal("29.1244229");

        double distance = distanceCalculator.calculate(
                lat1,
                lng1,
                lat2,
                lng2
        );

        assertEquals(452.73, distance, 1.0);
    }
}
