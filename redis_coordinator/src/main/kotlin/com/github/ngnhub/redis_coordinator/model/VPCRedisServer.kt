package com.github.ngnhub.redis_coordinator.model

import com.github.ngnhub.redis_coordinator.utils.MIGRATION_TIMEOUT
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.params.MigrateParams

/**
 * For cases when access to nodes from the outside and their internal interaction occur at different addresses.
 */
class VPCRedisServer(
    host: String,
    port: Int,
    redistributePageSize: Int,
    redisPool: JedisPool = JedisPool(JedisPoolConfig(), host, port),
    val privateHost: String = host,
    val privatePort: Int = port
) : RedisServer(host, port, redistributePageSize, redisPool) {

    override fun migrate(fromServiceResource: Jedis, migrateParams: MigrateParams, keysForMigration: Array<String>) {
        fromServiceResource.migrate(privateHost, privatePort, MIGRATION_TIMEOUT, migrateParams, *keysForMigration)
    }
}
