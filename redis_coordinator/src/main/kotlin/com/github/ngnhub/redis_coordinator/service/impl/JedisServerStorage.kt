package com.github.ngnhub.redis_coordinator.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ngnhub.redis_coordinator.config.JedisServerStorageProperty
import com.github.ngnhub.redis_coordinator.service.ServerStorage
import com.github.ngnhub.redis_coordinator.service.StorableRedisServer
import org.springframework.stereotype.Service
import redis.clients.jedis.JedisPool
import redis.clients.jedis.params.ScanParams

// todo: need smth more clever
@Service
class JedisServerStorage(
    jedisServerStorageProperty: JedisServerStorageProperty,
    private val mapper: ObjectMapper
) : ServerStorage {
    private val jedisPool = JedisPool(jedisServerStorageProperty.host, jedisServerStorageProperty.port)

    override fun getAll(): List<StorableRedisServer> {
        val param = ScanParams().count(10)
        val servers = mutableListOf<StorableRedisServer>()
        jedisPool.resource.use { jedis ->
            var hasValues = true
            var cursor = ScanParams.SCAN_POINTER_START
            while (hasValues) {
                val scan = jedis.scan(cursor, param)
                jedis.mget(*scan.result.toTypedArray()).asSequence()
                    .map { server -> mapper.readValue(server.toString(), StorableRedisServer::class.java) }
                    .forEach { server -> servers.add(server) }
                cursor = scan.cursor
                hasValues = cursor != ScanParams.SCAN_POINTER_START
            }
        }
        return servers
    }

    override fun set(key: String, server: StorableRedisServer) {
        jedisPool.resource.use { jedis ->
            val value = StorableRedisServer(server.host, server.port, server.redistributePageSize)
            jedis.set(key, mapper.writeValueAsString(value))
        }
    }

    override fun minus(key: String) {
        jedisPool.resource.use { jedis ->
            jedis.del(key)
        }
    }

    override fun get(key: String): StorableRedisServer? {
        jedisPool.resource.use { jedis ->
            val result = jedis.get(key)
            if (result != null) {
                return mapper.readValue(result.toString(), StorableRedisServer::class.java)
            }
        }
        return null
    }
}
