package com.jinternals.scheduler.controllers;

import com.jinternals.scheduler.controllers.request.ScheduleItem;
import com.jinternals.scheduler.exceptions.ScheduleItemNotFound;
import com.jinternals.scheduler.services.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(path = "/api")
public class SchedulerController {

    private SchedulerService schedulerService;

    @Autowired
    public SchedulerController(SchedulerService schedulerService){
        this.schedulerService = schedulerService;
    }

    @PostMapping( value = "/schedule", produces = APPLICATION_JSON_VALUE)
    public void schedule(@RequestBody ScheduleItem scheduleItem) {
        schedulerService.scheduleItem(scheduleItem);
    }

    @GetMapping(value = "/schedule/{id}", produces = APPLICATION_JSON_VALUE)
    public ScheduleItem scheduleItems(@PathVariable("id") String id) throws ScheduleItemNotFound {
        return schedulerService.getScheduleItem(id);
    }


}
