package com.example.botpoliclinica.dto;

import lombok.Data;

@Data
public class LpusDto {
    private Integer id;
    private Integer districtId;
    private String lpuFullName;
    private String address;
    private String lpuType;
}
