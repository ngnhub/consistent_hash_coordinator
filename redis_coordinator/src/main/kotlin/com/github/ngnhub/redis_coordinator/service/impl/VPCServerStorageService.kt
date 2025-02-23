package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.partition_coordinator.impl.DefaultCoordinator
import com.github.ngnhub.redis_coordinator.model.VPCRedisServer
import com.github.ngnhub.redis_coordinator.model.VPCRedisServerDto
import com.github.ngnhub.redis_coordinator.service.ServerStorage
import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import com.github.ngnhub.redis_coordinator.utils.VPC_PROFILE
import com.github.ngnhub.redis_coordinator.utils.toServer
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile(VPC_PROFILE)
class VPCServerStorageService(
    val coordinator: DefaultCoordinator<VPCRedisServer>,
    val serverStorage: ServerStorage<VPCRedisServerDto>
) : ServerStorageService<VPCRedisServer, VPCRedisServerDto> {

    @PostConstruct
    fun init() {
        serverStorage.getAll(VPCRedisServerDto::class.java).forEach { addServer(it) }
    }

    override fun addServer(redisServerDto: VPCRedisServerDto) {
        val server = redisServerDto.toServer()
        coordinator + server
        serverStorage[server.key] = redisServerDto
    }

    override fun get(key: String): VPCRedisServer {
        return coordinator[key]
    }

    override fun minus(key: String) {
        coordinator - key
        serverStorage - key
    }

    override fun isAlive(host: String, port: Int): Boolean {
        serverStorage[host + port, VPCRedisServerDto::class.java]?.let {
            return this[host + port].health()
        }
        return false
    }
}
