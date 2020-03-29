package com.jinternals.scheduler.services;

import com.jinternals.scheduler.constants.SchedulerConstants;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.jinternals.scheduler.constants.SchedulerConstants.BUCKET_ID_FORMAT;

@Service
public class BucketService {

    public String getBucketId(LocalDateTime dateTime){
        return dateTime.format(BUCKET_ID_FORMAT);
    }

}
