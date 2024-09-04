package com.E1i3.NoExit.domain.store.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResDto {

    private Long id;
    private String storeName;
    private Double latitude;  // 위도
    private Double longitude; // 경도

}