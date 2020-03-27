package com.jinternals.scheduler.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SchedulerService {

    @Scheduled(cron = "0 * * * * ?")
    public void perMinute() {
        System.out.println(LocalDateTime.now());
    }
}
