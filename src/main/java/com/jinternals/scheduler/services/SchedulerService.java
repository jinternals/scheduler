package com.jinternals.scheduler.services;

import com.jinternals.scheduler.controllers.request.ScheduleItem;
import com.jinternals.scheduler.repositories.ScheduleItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.UUID;

import static java.time.Duration.ofMillis;
import static java.time.LocalDateTime.ofInstant;
import static org.springframework.data.redis.connection.stream.Consumer.from;
import static org.springframework.data.redis.connection.stream.StreamOffset.fromStart;
import static org.springframework.data.redis.stream.StreamMessageListenerContainer.create;
import static org.springframework.util.StringUtils.isEmpty;

@Service
@Slf4j
public class SchedulerService {

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    @Autowired
    private ScheduleItemRepository scheduleItemRepository;

    @Autowired
    private BucketService bucketService;

    @Scheduled(cron = "0 * * * * ?")
    public void perMinute() {
        String bucketId = bucketService.getBucketId(LocalDateTime.now());

        log.info("Processing bucket with id {}", bucketId);

        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(ofMillis(100))
                .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = create(connectionFactory, containerOptions);

        container.receive(from("scheduler-bucket-group", "scheduler-bucket"),
                fromStart(bucketId),
                record -> {
                    System.out.println(record);
                });

    }

    public void scheduleItem(ScheduleItem scheduleItem) {
        if(isEmpty(scheduleItem.getId()))
        {
            scheduleItem.setId(UUID.randomUUID().toString());
        }
        String bucketId = bucketService.getBucketId(toLocalDateTime(scheduleItem.getTriggerTime()));
        ScheduleItem savedScheduleItem = scheduleItemRepository.save(scheduleItem);
        StringRecord record = StreamRecords.string(Collections.singletonMap("scheduleItemId",savedScheduleItem.getId())).withStreamKey(bucketId);
        redisTemplate.opsForStream().add(record);

    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return ofInstant(instant, ZoneOffset.UTC);
    }
}
