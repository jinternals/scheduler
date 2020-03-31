package com.jinternals.scheduler.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.of;

class BucketServiceTest {

    private BucketService bucketService = new BucketService();

    @Test
    void shouldGenerateBucketId() {
        LocalDateTime localDateTime = of(2020,4,2,3,5);
        String bucketId = bucketService.getBucketId(localDateTime);
        Assertions.assertThat(bucketId).isEqualTo("202004020305");
    }
}
