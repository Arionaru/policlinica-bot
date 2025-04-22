package com.example.botpoliclinica.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDto {
    private String id;
    private String name;
    private Integer freeParticipantCount;
    private LocalDate nearestDate;
    private Integer freeTicketCount;
}
