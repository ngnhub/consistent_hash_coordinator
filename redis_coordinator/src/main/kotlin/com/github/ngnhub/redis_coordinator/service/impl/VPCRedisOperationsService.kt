package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.redis_coordinator.model.VPCRedisServer
import com.github.ngnhub.redis_coordinator.model.VPCRedisServerDto
import com.github.ngnhub.redis_coordinator.service.RedisOperationsService
import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import com.github.ngnhub.redis_coordinator.utils.VPC_PROFILE
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile(VPC_PROFILE)
class VPCRedisOperationsService(private val serverStorageService: ServerStorageService<VPCRedisServer, VPCRedisServerDto>) :
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
