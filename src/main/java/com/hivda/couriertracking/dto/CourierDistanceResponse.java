package com.hivda.couriertracking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourierDistanceResponse {

    private String courierId;
    private double totalDistanceMeters;

}
