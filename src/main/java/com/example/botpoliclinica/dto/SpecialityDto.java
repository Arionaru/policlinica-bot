package com.example.botpoliclinica.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpecialityDto {
    private Integer id;
    private String name;
    private Integer countFreeTicket;
    private LocalDateTime nearestDate;
}
