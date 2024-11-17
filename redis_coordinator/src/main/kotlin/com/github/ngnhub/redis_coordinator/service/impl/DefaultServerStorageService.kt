package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.partition_coordinator.impl.DefaultCoordinator
import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.service.ServerStorage
import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import com.github.ngnhub.redis_coordinator.service.StorableRedisServer
import org.springframework.stereotype.Service

@Service
class DefaultServerStorageService(
    private val coordinator: DefaultCoordinator<RedisServer>,
    private val serverStorage: ServerStorage
) : ServerStorageService {


    override fun addServer(host: String, port: Int) {
        val server = RedisServer(host, port, 100)
        coordinator + server
        serverStorage[server.key] = StorableRedisServer(server.host, server.port, server.redistributePageSize)
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
            return RedisServer(it.host, it.port, it.redistributePageSize).health()
        }
        return false
    }
}
