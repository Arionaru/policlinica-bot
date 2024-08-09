package com.example.botpoliclinica.scheduler;

import com.example.botpoliclinica.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final AppointmentService appointmentService;

    @Scheduled(cron = "*/30 * * * * *")
    public void go() throws IOException, LineUnavailableException {
        System.out.println("go");
        appointmentService.getAppointment();
    }
}
