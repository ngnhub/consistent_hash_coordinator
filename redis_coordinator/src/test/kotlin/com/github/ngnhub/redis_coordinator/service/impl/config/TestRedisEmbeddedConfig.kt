package com.github.ngnhub.redis_coordinator.service.impl.config

import com.github.ngnhub.redis_coordinator.config.JedisServerStorageProperty
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer

@TestConfiguration
class TestRedisEmbeddedConfig(jedisServerStorageProperty: JedisServerStorageProperty) {

    val redis: RedisServer = RedisServer(jedisServerStorageProperty.port)

    @PostConstruct
    fun init() = redis.start()

    @PreDestroy
    fun destroy() = redis.stop()
}
