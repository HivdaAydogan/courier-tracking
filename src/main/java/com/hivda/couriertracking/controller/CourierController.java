package com.hivda.couriertracking.controller;

import com.hivda.couriertracking.dto.CourierDistanceResponse;
import com.hivda.couriertracking.dto.CourierLocationRequest;
import com.hivda.couriertracking.service.CourierLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierLocationService locationService;

    @PostMapping("/locations")
    public ResponseEntity<Void> addLocation(@Valid @RequestBody CourierLocationRequest request) {
        locationService.processLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{courierId}/distance")
    public ResponseEntity<CourierDistanceResponse> getTotalTravelDistance(@PathVariable String courierId) {
        double totalDistance = locationService.getTotalTravelDistance(courierId);
        return ResponseEntity.ok(new CourierDistanceResponse(courierId, totalDistance));
    }

}
