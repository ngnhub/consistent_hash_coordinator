package com.github.ngnhub.redis_coordinator.model

import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.partition_coordinator.Server
import io.github.oshai.kotlinlogging.KotlinLogging
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.params.MigrateParams
import redis.clients.jedis.params.ScanParams
import java.math.BigInteger

val logger = KotlinLogging.logger {}

class RedisServer(
    host: String,
    port: Int,
    val privateHost: String = host,
    val privatePort: Int = port,
    val redistributePageSize: Int,
    val redisPool: JedisPool = JedisPool(JedisPoolConfig(), host, port) // todo async lib
) : Server(host, port) {

    override fun reDistribute(from: Server, by: HashFunction<String>) {
        val redisServer = from as RedisServer
        redisServer.redisPool.resource.use { resource -> migrateBatched(by, resource, from.hash) }
    }

    private fun migrateBatched(
        hashFunction: HashFunction<String>,
        fromServiceResource: Jedis,
        fromServerHash: BigInteger
    ) {
        var cursor = ScanParams.SCAN_POINTER_START
        var hasValue = true

        while (hasValue) {
            val scan = fromServiceResource.scan(cursor, ScanParams().count(redistributePageSize))
            val migrateParams = MigrateParams()
            val timeout = 3000 // todo: magic number.. how to chose? how to handle if it timed out
            val keysForMigration = scan.result.asSequence()
                .filter { keyOfValue -> isHashInside(fromServerHash, hashFunction.hash(keyOfValue)) }
                .toSet()
                .toTypedArray()
            fromServiceResource.migrate(privateHost, privatePort, timeout, migrateParams, *keysForMigration)
            // todo what if key is already removed
            cursor = scan.cursor
            hasValue = cursor != ScanParams.SCAN_POINTER_START
            logger.info { "Migrated ${scan.result.size}" }
        }
    }

    private fun isHashInside(fromServerHash: BigInteger, keyOfValueHash: BigInteger): Boolean {
        if (hash > fromServerHash) {
            return keyOfValueHash > fromServerHash && keyOfValueHash <= hash
        }
        if (hash < fromServerHash) {
            return keyOfValueHash <= hash || keyOfValueHash > fromServerHash
        }
        throw IllegalArgumentException("Can not redistribute within one server")
    }
}
