package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.partition_coordinator.impl.DefaultCoordinator
import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import org.springframework.stereotype.Service

@Service
class DefaultServerStorageService(private val coordinator: DefaultCoordinator<RedisServer>) : ServerStorageService {

    private val tempMap = mutableMapOf<String, RedisServer>() //todo: it should be persist somewhere. db? redis it self?

    override fun addServer(host: String, port: Int) {
        val server = RedisServer(host, port, 100)
        coordinator + server
        tempMap[server.key] = server
    }

    override fun get(key: String): RedisServer {
        return coordinator[key]
    }

    override fun isAlive(host: String, port: Int): Boolean {
        tempMap[host + port]?.let {
            return it.health()
        }
        return false
    }
}
