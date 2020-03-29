package com.jinternals.scheduler;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
public class SchedulerApplication {

	public static void main(String[] args) {
		run(SchedulerApplication.class, args);
	}

}
