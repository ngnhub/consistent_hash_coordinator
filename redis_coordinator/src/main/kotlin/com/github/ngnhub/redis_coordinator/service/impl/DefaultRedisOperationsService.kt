package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.redis_coordinator.service.RedisOperationsService
import com.github.ngnhub.redis_coordinator.service.ServerSearchService
import com.github.ngnhub.redis_coordinator.utils.readAll
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class DefaultRedisOperationsService(private val serverSearchService: ServerSearchService) : RedisOperationsService {

    companion object {
        val logger = KotlinLogging.logger {}
    }

    override fun get(key: String): String = serverSearchService[key].redisPool.resource.use { redis ->
        val value = redis.get(key)
        logger.info { "$key retrieved" }
        return value
    }

    override fun getAll(serverKey: String) = readAll(serverSearchService[serverKey].redisPool, 10) { it }

    override fun set(key: String, value: String) {
        serverSearchService[key].redisPool.resource.use { redis ->
            redis.set(key, value)
            logger.info { "$key to $value added" }
        }
    }

    override fun minus(key: String) {
        serverSearchService[key]
        logger.info { "$key removed" }
    }
}
