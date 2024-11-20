package com.github.ngnhub.redis_coordinator.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ngnhub.redis_coordinator.config.JedisServerStorageProperty
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.service.ServerStorage
import com.github.ngnhub.redis_coordinator.utils.readAll
import org.springframework.stereotype.Service
import redis.clients.jedis.JedisPool

// todo: need smth more clever (like hearth bits?)
@Service
class JedisServerStorage(
    jedisServerStorageProperty: JedisServerStorageProperty,
    private val mapper: ObjectMapper
) : ServerStorage {
    private val jedisPool = JedisPool(jedisServerStorageProperty.host, jedisServerStorageProperty.port)

    override fun getAll(): List<RedisServerDto> {
        return readAll(jedisPool, 10) { mapper.readValue(it, RedisServerDto::class.java) }
    }

    override fun set(key: String, server: RedisServerDto) {
        jedisPool.resource.use { jedis ->
            val value = RedisServerDto(server.host, server.port, server.redistributePageSize)
            jedis.set(key, mapper.writeValueAsString(value))
        }
    }

    override fun minus(key: String) {
        jedisPool.resource.use { jedis ->
            jedis.del(key)
        }
    }

    override fun get(key: String): RedisServerDto? {
        jedisPool.resource.use { jedis ->
            val result = jedis.get(key)
            if (result != null) {
                return mapper.readValue(result.toString(), RedisServerDto::class.java)
            }
        }
        return null
    }
}
