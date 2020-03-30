package com.jinternals.scheduler.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.jinternals.scheduler.constants.SchedulerConstants.SCHEDULED_ITEMS_GROUP_NAME;
import static com.jinternals.scheduler.constants.SchedulerConstants.SCHEDULED_ITEMS_STREAM_NAME;

@Component
public class RedisInitializer {

    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        try {
            redisTemplate.opsForStream().createGroup(SCHEDULED_ITEMS_STREAM_NAME, SCHEDULED_ITEMS_GROUP_NAME);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    @Autowired
    public RedisInitializer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}
