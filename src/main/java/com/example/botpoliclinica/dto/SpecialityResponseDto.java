package com.example.botpoliclinica.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpecialityResponseDto {
    private List<SpecialityDto> result;
    private Boolean success;
    private Integer errorCode;
    private String message;
    private String stackTrace;
}
