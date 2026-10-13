package com.hivda.couriertracking.service;

import java.math.BigDecimal;

public interface DistanceCalculator {

    double calculate(
            BigDecimal lat1,
            BigDecimal lng1,
            BigDecimal lat2,
            BigDecimal lng2);

}
