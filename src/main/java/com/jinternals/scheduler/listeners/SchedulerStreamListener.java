package com.jinternals.scheduler.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SchedulerStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    private StringRedisTemplate redisTemplate;

    @Autowired
    public SchedulerStreamListener(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String id = message.getValue().get("id");
        log.info("ID {}",id);
    }

}
