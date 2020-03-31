package com.jinternals.scheduler.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(code = NOT_FOUND)
public class ScheduleItemNotFound extends Exception {

    public ScheduleItemNotFound(String message){
        super(message);
    }
}
