package com.hivda.couriertracking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class StoreData {

    private String name;
    private BigDecimal lat;
    private BigDecimal lng;

}
