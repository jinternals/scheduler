package com.jinternals.scheduler.services;

import com.jinternals.scheduler.constants.SchedulerConstants;
import com.jinternals.scheduler.controllers.request.ScheduleItem;
import com.jinternals.scheduler.exceptions.ScheduleItemNotFound;
import com.jinternals.scheduler.repositories.ScheduleItemRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.jinternals.scheduler.constants.SchedulerConstants.ID;
import static com.jinternals.scheduler.constants.SchedulerConstants.SCHEDULED_ITEMS_STREAM_NAME;
import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.ofInstant;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static org.springframework.data.redis.connection.stream.MapRecord.create;
import static org.springframework.data.redis.connection.stream.StreamRecords.newRecord;
import static org.springframework.util.StringUtils.isEmpty;

@Service
@Slf4j
public class SchedulerService {

    private ScheduleItemRepository scheduleItemRepository;

    private StringRedisTemplate redisTemplate;

    private BucketService bucketService;

    @Autowired
    public SchedulerService(ScheduleItemRepository scheduleItemRepository,
                            StringRedisTemplate redisTemplate,
                            BucketService bucketService){
        this.scheduleItemRepository = scheduleItemRepository;
        this.redisTemplate = redisTemplate;
        this.bucketService = bucketService;
    }

    @Scheduled(cron = "0 * * * * ?")
    @SchedulerLock(name = "scheduled-items-lock")
    public void perMinute() {
        String bucketId = bucketService.getBucketId(now());
        while (true) {
            log.info("Processing bucket with id {}", bucketId);
            String id = redisTemplate.opsForSet().pop(bucketId);
            if (nonNull(id)) {
                log.info("Processing scheduled item with id {}", id);
                redisTemplate.opsForStream().add(create(SCHEDULED_ITEMS_STREAM_NAME, singletonMap(ID, id)));
            } else return;
        }

    }

    @Transactional
    public void scheduleItem(ScheduleItem scheduleItem) {
        if (isEmpty(scheduleItem.getId())) {
            scheduleItem.setId(UUID.randomUUID().toString());
        }
        String bucketId = bucketService.getBucketId(toLocalDateTime(scheduleItem.getTriggerTime()));
        log.info("Scheduling item in bucket id {}", bucketId);
        ScheduleItem saved = scheduleItemRepository.save(scheduleItem);
        redisTemplate.opsForSet().add(bucketId, saved.getId());
    }

    public ScheduleItem getScheduleItem(String id) throws ScheduleItemNotFound {
        return scheduleItemRepository.findById(id).orElseThrow(() -> new ScheduleItemNotFound());
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return ofInstant(instant, ZoneOffset.UTC);
    }
}
