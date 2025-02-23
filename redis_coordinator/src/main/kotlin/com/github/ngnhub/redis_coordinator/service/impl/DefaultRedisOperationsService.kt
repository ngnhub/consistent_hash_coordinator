package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.service.RedisOperationsService
import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import com.github.ngnhub.redis_coordinator.utils.DEFAULT_PROFILE
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile(DEFAULT_PROFILE)
class DefaultRedisOperationsService(private val serverStorageService: ServerStorageService<RedisServer, RedisServerDto>) :
    RedisOperationsService {

    override fun get(key: String): String = serverStorageService[key].redisPool.resource.use { redis ->
        return redis.get(key)
    }

    override fun set(key: String, value: String): Unit = serverStorageService[key].redisPool.resource.use { redis ->
        redis.set(key, value)
    }

    override fun minus(key: String) {
        serverStorageService[key]
    }
}
