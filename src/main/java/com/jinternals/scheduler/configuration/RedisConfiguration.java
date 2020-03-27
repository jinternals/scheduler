package com.jinternals.scheduler.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
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

}
