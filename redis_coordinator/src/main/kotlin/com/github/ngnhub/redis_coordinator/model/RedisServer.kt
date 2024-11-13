package com.github.ngnhub.redis_coordinator.model

import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.partition_coordinator.Server
import io.github.oshai.kotlinlogging.KotlinLogging
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.params.MigrateParams
import redis.clients.jedis.params.ScanParams
import redis.clients.jedis.resps.ScanResult

val logger = KotlinLogging.logger {}

class RedisServer(
    host: String,
    port: Int,
    private val redistributePageSize: Int,
    private val redisPool: JedisPool = JedisPool(JedisPoolConfig(), host, port) // todo async lib
) : Server(host, port) {

    override fun reDistribute(from: Server, by: HashFunction<String>) {
        val redisServer = from as RedisServer
        redisServer.redisPool.resource.use { resource -> migrateBatched(by, resource) }
    }

    private fun migrateBatched(hashFunction: HashFunction<String>, resource: Jedis) {
        var scan = resource.scan("0", ScanParams().count(redistributePageSize).match("*"))
        while (scan.isNotEmpty()) {
            val migrateParams = MigrateParams().copy()
            val timeout = 3000 // todo: how to chose? how to handle if timed out
            val keysForMigration = scan.result
                .filter { key -> hashFunction.hash(key) <= hash }
                .toTypedArray()
            // todo doesnt work with localhost need to run in a container
            resource.migrate(host, port, timeout, migrateParams, *keysForMigration)
            // todo what if key is already removed
            logger.info { "Migrated ${scan.result.size}" }
            scan = resource.scan(scan.cursor)
        }
    }

    private fun ScanResult<String>.isNotEmpty() = this.result.isNotEmpty()

    fun read(key: String): Any? = redisPool.resource.use { redis ->
        return redis.get(key)
    }

    fun insert(key: String, value: String): Unit = redisPool.resource.use { redis ->
        redis.set(key, value)
    }
}
