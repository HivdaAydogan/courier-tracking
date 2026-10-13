package com.hivda.couriertracking;

import com.hivda.couriertracking.dto.CourierLocationRequest;
import com.hivda.couriertracking.entity.CourierLocation;
import com.hivda.couriertracking.mapper.CourierTrackingMapper;
import com.hivda.couriertracking.repository.CourierLocationRepository;
import com.hivda.couriertracking.service.CourierLocationService;
import com.hivda.couriertracking.service.DistanceCalculator;
import com.hivda.couriertracking.service.StoreEntranceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierLocationServiceTest {

    @Mock
    private CourierLocationRepository repository;

    @Mock
    private CourierTrackingMapper mapper;

    @Mock
    private StoreEntranceService entranceService;

    @Mock
    private DistanceCalculator distanceCalculator;

    @InjectMocks
    private CourierLocationService courierLocationService;

    private CourierLocation previousLocation;
    private CourierLocation currentLocation;

    @BeforeEach
    void setUp() {

        previousLocation = new CourierLocation(
                "C100",
                Instant.parse("2026-10-06T15:00:00Z"),
                new BigDecimal("40.9900000"),
                new BigDecimal("29.1200000")
        );

        currentLocation = new CourierLocation(
                "C100",
                Instant.parse("2026-10-06T15:00:20Z"),
                new BigDecimal("40.9923307"),
                new BigDecimal("29.1244229")
        );
    }

    @Test
    void shouldProcessLocation() {

        CourierLocationRequest request = new CourierLocationRequest();
        request.setCourierId("C100");
        request.setTimestamp(
                Instant.parse("2026-10-06T15:00:20Z")
        );
        request.setLatitude(
                new BigDecimal("40.9923307")
        );
        request.setLongitude(
                new BigDecimal("29.1244229")
        );

        when(repository.findTopByCourierIdOrderByTimestampDesc("C100"))
                .thenReturn(Optional.of(previousLocation));

        when(mapper.toEntity(request))
                .thenReturn(currentLocation);

        courierLocationService.processLocation(request);

        verify(repository).save(currentLocation);

        verify(entranceService).checkStoreEntrances(
                currentLocation,
                previousLocation
        );
    }

    @Test
    void shouldCalculateTotalTravelDistance() {

        CourierLocation location1 = new CourierLocation(
                "C100",
                Instant.parse("2026-10-06T15:00:00Z"),
                new BigDecimal("40.9900000"),
                new BigDecimal("29.1200000")
        );

        CourierLocation location2 = new CourierLocation(
                "C100",
                Instant.parse("2026-10-06T15:00:20Z"),
                new BigDecimal("40.9923307"),
                new BigDecimal("29.1244229")
        );

        CourierLocation location3 = new CourierLocation(
                "C100",
                Instant.parse("2026-10-06T15:00:40Z"),
                new BigDecimal("40.9900000"),
                new BigDecimal("29.1200000")
        );

        when(repository.findByCourierIdOrderByTimestampAsc("C100"))
                .thenReturn(List.of(
                        location1,
                        location2,
                        location3
                ));

        when(distanceCalculator.calculate(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(452.73);

        double result =
                courierLocationService.getTotalTravelDistance("C100");

        assertEquals(905.46, result, 0.01);

        verify(distanceCalculator, times(2))
                .calculate(any(), any(), any(), any());
    }

    @Test
    void shouldReturnZeroWhenCourierHasNoLocations() {

        when(repository.findByCourierIdOrderByTimestampAsc("C100"))
                .thenReturn(List.of());

        double result =
                courierLocationService.getTotalTravelDistance("C100");

        assertEquals(0.0, result);

        verifyNoInteractions(distanceCalculator);
    }
}
