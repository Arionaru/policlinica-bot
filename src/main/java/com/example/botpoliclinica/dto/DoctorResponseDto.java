package com.example.botpoliclinica.dto;

import lombok.Data;

import java.util.List;

@Data
public class DoctorResponseDto {
    private List<DoctorDto> result;
    private Boolean success;
    private Integer errorCode;
    private String message;
    private String stackTrace;
}
