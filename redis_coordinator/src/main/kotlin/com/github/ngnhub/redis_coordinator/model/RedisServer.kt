package com.github.ngnhub.redis_coordinator.model

import com.github.ngnhub.partition_coordinator.Server
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class RedisServer(
    override val host: String,
    override val port: Int,
    override val virtualNodesKeys: Set<String> = mutableSetOf(),
) : Server {

    private val redisTemplate = JedisPool(JedisPoolConfig(), host, port);

    override fun insert(key: String, value: Any?) {
        redisTemplate.resource.use { redis -> redis.set(key, value.toString()) }
        value?.let { }
    }

    override fun reDistribute(from: Server) {
        TODO("Not yet implemented")
    }

    override fun read(key: String): Any? {
        return redisTemplate.resource.use { redis -> redis.get(key) }
    }
}