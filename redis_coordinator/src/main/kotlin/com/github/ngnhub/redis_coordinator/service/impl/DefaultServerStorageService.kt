package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.partition_coordinator.impl.DefaultCoordinator
import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.service.ServerStorage
import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import com.github.ngnhub.redis_coordinator.utils.toServer
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class DefaultServerStorageService(
    private val coordinator: DefaultCoordinator<RedisServer>,
    private val serverStorage: ServerStorage
) : ServerStorageService {

    @PostConstruct
    fun init() {
        serverStorage.getAll().forEach { addServer(it) }
    }

    override fun addServer(redisServerDto: RedisServerDto) {
        val server = redisServerDto.toServer()
        coordinator + server
        serverStorage[server.key] = redisServerDto
    }

    override fun get(key: String): RedisServer {
        return coordinator[key]
    }

    override fun minus(key: String) {
        coordinator - key
        serverStorage - key
    }

    override fun isAlive(host: String, port: Int): Boolean {
        serverStorage[host + port]?.let {
            return this[host + port].health()
        }
        return false
    }
}
