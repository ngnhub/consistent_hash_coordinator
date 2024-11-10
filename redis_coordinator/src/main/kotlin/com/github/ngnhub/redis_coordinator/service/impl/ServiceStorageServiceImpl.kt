package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.partition_coordinator.impl.DefaultCoordinator
import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.service.ServiceStorageService
import org.springframework.stereotype.Service

@Service
class ServiceStorageServiceImpl(private val coordinator: DefaultCoordinator) : ServiceStorageService {

    private val tempMap = mutableMapOf<String, RedisServer>() //todo: it should be persist somewhere. db? redis it self?

    override fun addServer(host: String, port: Int) {
        val server = RedisServer(host, port)
        coordinator.addServer(server)
        tempMap[server.key] = server
    }

    override fun read(key: String): String {
       return coordinator[key] as String
    }

    override fun isAlive(host: String, port: Int): Boolean {
        tempMap[host + port]?.let {
            return it.health()
        }
        return false
    }
}
