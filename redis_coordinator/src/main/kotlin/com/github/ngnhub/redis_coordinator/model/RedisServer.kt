package com.github.ngnhub.redis_coordinator.model

import com.github.ngnhub.partition_coordinator.Server
import io.github.oshai.kotlinlogging.KotlinLogging
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

private val logger = KotlinLogging.logger {}

class RedisServer(
    private val host: String,
    private val port: Int,
    override val virtualNodesKeys: Set<String> = mutableSetOf(),
) : Server<String> {

    private val redisTemplate = JedisPool(JedisPoolConfig(), host, port);

    companion object {
        const val CONNECTION_TIME_OUT = 3000
    }

    override val key
        get() = host + port

    override fun health(): Boolean {
        Socket().use { socket ->
            try {
                socket.connect(InetSocketAddress(host, port), CONNECTION_TIME_OUT);
                return true
            } catch (ex: IOException) {
                logger.error(ex) { "Error while connecting to $host:$port" }
                return false
            }
        }
    }

    override fun insert(key: String, value: Any?) {
        redisTemplate.resource.use { redis -> redis.set(key, value.toString()) }
        value?.let { }
    }

    override fun reDistribute(from: Server<String>) {
        TODO("Not yet implemented")
    }

    override fun read(key: String): Any? {
        return redisTemplate.resource.use { redis -> redis.get(key) }
    }
}