package com.github.ngnhub.redis_coordinator.model

import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.partition_coordinator.Server
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.params.MigrateParams
import redis.clients.jedis.params.ScanParams

class RedisServer(
    host: String,
    port: Int,
    private val redistributePageSize: Int,
    private val redisPool: JedisPool = JedisPool(JedisPoolConfig(), host, port) // todo async lib
) : Server(host, port) {

    override fun reDistribute(from: Server, by: HashFunction<String>) {
        val count = ScanParams().count(redistributePageSize)
        val redisServer = from as RedisServer
        redisServer.redisPool.resource.use { resource -> migrateBatched(by, resource, count) }
    }

    private fun migrateBatched(by: HashFunction<String>, resource: Jedis, count: ScanParams?) {
        var scan = resource.scan("0")
        while (scan != null) {
            val keysForMigration = scan.result
                .filter { key -> by.hash(key) <= hash }
                .toTypedArray()
            val migrateParams = MigrateParams().copy()
            val timeout = 3000 // todo: how to chose? how to handle if timed out
            resource.migrate(host, port, timeout, migrateParams, *keysForMigration) // todo what if key is already removed
            scan = resource.scan(scan.cursor, count)
        }
    }
}
