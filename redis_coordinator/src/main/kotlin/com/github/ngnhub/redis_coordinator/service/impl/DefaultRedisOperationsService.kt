package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.redis_coordinator.service.RedisOperationsService
import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import org.springframework.stereotype.Service

@Service
class DefaultRedisOperationsService(private val serverStorageService: ServerStorageService) : RedisOperationsService {

    override fun get(key: String): Any? {
        return serverStorageService[key].read(key)
    }

    override fun set(key: String, value: Any?) {
        serverStorageService[key].insert(key, value.toString())
    }
}
