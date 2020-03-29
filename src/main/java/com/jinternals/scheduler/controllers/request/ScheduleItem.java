package com.jinternals.scheduler.controllers.request;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@Data
@RedisHash("scheduled-items")
public class ScheduleItem implements Serializable {
    @Id private String id;
    private Instant triggerTime;
}
