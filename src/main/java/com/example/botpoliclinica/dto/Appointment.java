package com.example.botpoliclinica.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Appointment {
    private String id;
    private LocalDateTime visitStart;
    private LocalDateTime visitEnd;
    private String address;
    private String number;
    private String room;
}
