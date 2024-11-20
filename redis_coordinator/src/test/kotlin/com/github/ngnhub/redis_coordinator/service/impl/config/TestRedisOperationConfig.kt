package com.github.ngnhub.redis_coordinator.service.impl.config

import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.partition_coordinator.impl.DefaultCoordinator
import com.github.ngnhub.redis_coordinator.model.RedisServer
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestRedisOperationConfig {

    @Bean
    fun mockedHashFunction(): HashFunction<String> = mock()

    @Bean
    @Primary
    fun testCoordinator(mockedHashFunction: HashFunction<String>) = DefaultCoordinator<RedisServer>(mockedHashFunction)
}
