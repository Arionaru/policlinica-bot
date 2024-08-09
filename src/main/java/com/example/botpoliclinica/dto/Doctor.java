package com.example.botpoliclinica.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor {
    String id;
    String name;
    Integer freeParticipantCount;
    LocalDate nearestDate;
    Integer freeTicketCount;
}
