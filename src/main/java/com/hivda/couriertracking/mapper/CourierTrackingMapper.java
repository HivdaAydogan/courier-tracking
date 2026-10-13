package com.hivda.couriertracking.mapper;

import com.hivda.couriertracking.dto.CourierLocationRequest;
import com.hivda.couriertracking.entity.CourierLocation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourierTrackingMapper {

    CourierLocation toEntity(CourierLocationRequest request);
}
