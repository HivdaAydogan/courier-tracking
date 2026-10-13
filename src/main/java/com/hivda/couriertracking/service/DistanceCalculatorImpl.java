package com.hivda.couriertracking.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DistanceCalculatorImpl implements DistanceCalculator {

    private static final double EARTH_RADIUS_METERS = 6_371_000;


    @Override
    public double calculate(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        double latitude1 = Math.toRadians(lat1.doubleValue());
        double latitude2 = Math.toRadians(lat2.doubleValue());

        double deltaLatitude = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLongitude = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());

        double a = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                + Math.cos(latitude1)
                * Math.cos(latitude2)
                * Math.sin(deltaLongitude / 2)
                * Math.sin(deltaLongitude / 2);

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

}
