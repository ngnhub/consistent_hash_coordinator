package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever
import com.github.ngnhub.partition_coordinator.impl.DefaultCoordinator
import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.service.ServerSearchService
import com.github.ngnhub.redis_coordinator.service.ServerStorage
import com.github.ngnhub.redis_coordinator.utils.toServer
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class DefaultServerSearchService(
    private val coordinator: DefaultCoordinator<RedisServer>,
    private val serverStorage: ServerStorage
) : ServerSearchService {

    companion object {
        val logger = KotlinLogging.logger {}
    }

    @PostConstruct
    fun init() {
        serverStorage.getAll().forEach { addServer(it) }
    }

    /**
     * @throws NoAvailableSever if no available servers found
     */
    override fun get(key: String): RedisServer {
        return coordinator[key]
    }

    override fun addServer(redisServerDto: RedisServerDto) {
        val server = redisServerDto.toServer()
        coordinator + server
        serverStorage[server.key] = redisServerDto
        logger.info { "Added server $redisServerDto" }
    }

    override fun minus(key: String) {
        coordinator - key
        serverStorage - key
        logger.info { "Removed server $key" }
    }

    override fun isAlive(host: String, port: Int): Boolean {
        serverStorage[host + port]?.let {
            return this[host + port].health()
        }
        return false
    }
}
