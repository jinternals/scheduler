package com.jinternals.scheduler.configuration;

import com.jinternals.scheduler.constants.SchedulerConstants;
import com.jinternals.scheduler.listeners.SchedulerStreamListener;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

import static com.jinternals.scheduler.constants.SchedulerConstants.SCHEDULED_ITEMS_GROUP_NAME;
import static com.jinternals.scheduler.constants.SchedulerConstants.SCHEDULED_ITEMS_STREAM_NAME;
import static org.springframework.data.redis.connection.stream.Consumer.from;
import static org.springframework.data.redis.connection.stream.StreamOffset.create;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "30s")
public class RedisConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(sentinel.getMaster());
        sentinel.getNodes()
                .forEach(s -> sentinelConfig.sentinel(s, Integer.valueOf(redisProperties.getPort())));
        sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return new LettuceConnectionFactory(sentinelConfig);
    }

    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory, "");
    }

    @Bean
    public StringRedisTemplate redisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory());
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean
    public Subscription subscription(SchedulerStreamListener schedulerStreamListener, RedisConnectionFactory redisConnectionFactory) throws InterruptedException {

        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = StreamMessageListenerContainerOptions
                .builder().pollTimeout(Duration.ofMillis(100)).build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = StreamMessageListenerContainer.create(redisConnectionFactory,
                containerOptions);

        Subscription subscription = container.receive(
                from(SCHEDULED_ITEMS_GROUP_NAME, "consumer-1"),
                create(SCHEDULED_ITEMS_STREAM_NAME, ReadOffset.lastConsumed()), schedulerStreamListener);

        container.start();

        return subscription;
    }

//    @Bean
//    public RedisTemplate<String, ScheduleItem> scheduledItemRedisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, ScheduleItem> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ScheduleItem.class));
//        redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());
//        redisTemplate.setHashValueSerializer(redisTemplate.getValueSerializer());
//        return redisTemplate;
//    }

}
