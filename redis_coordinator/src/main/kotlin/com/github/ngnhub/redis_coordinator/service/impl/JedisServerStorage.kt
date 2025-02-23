package com.github.ngnhub.redis_coordinator.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ngnhub.redis_coordinator.config.JedisServerStorageProperty
import com.github.ngnhub.redis_coordinator.service.ServerStorage
import com.github.ngnhub.redis_coordinator.utils.readAll
import redis.clients.jedis.JedisPool

// todo: need smth more clever (like hearth bits?)
class JedisServerStorage<T : Any>(
    jedisServerStorageProperty: JedisServerStorageProperty,
    private val mapper: ObjectMapper
) : ServerStorage<T> {

    private val jedisPool = JedisPool(jedisServerStorageProperty.host, jedisServerStorageProperty.port)

    override fun getAll(type: Class<T>): List<T> {
        return readAll(jedisPool, 10) { mapper.readValue(it, type) }
    }

    override fun set(key: String, server: T) {
        jedisPool.resource.use { jedis ->
            jedis.set(key, mapper.writeValueAsString(server))
        }
    }

    override fun minus(key: String) {
        jedisPool.resource.use { jedis ->
            jedis.del(key)
        }
    }

    override fun get(key: String, type: Class<T>): T? {
        jedisPool.resource.use { jedis ->
            val result = jedis.get(key)
            if (result != null) {
                return mapper.readValue(result.toString(), type)
            }
        }
        return null
    }
}
