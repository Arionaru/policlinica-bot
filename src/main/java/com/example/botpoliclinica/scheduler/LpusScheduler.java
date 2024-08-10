package com.example.botpoliclinica.scheduler;

import com.example.botpoliclinica.service.LpusService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LpusScheduler {
    private final LpusService lpusService;

    @Scheduled(cron = "@monthly")
    public void go() {
        lpusService.updateLpus();
    }
}
