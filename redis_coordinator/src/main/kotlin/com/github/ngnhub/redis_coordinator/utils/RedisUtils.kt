package com.github.ngnhub.redis_coordinator.utils

import redis.clients.jedis.JedisPool
import redis.clients.jedis.params.ScanParams

fun <T : Any> readAll(pool: JedisPool, pageSize: Int, convertFunction: (origin: String) -> T): List<T> {
    val param = ScanParams().count(pageSize)
    val result = mutableListOf<T>()
    pool.resource.use { jedis ->
        var hasValues = true
        var cursor = ScanParams.SCAN_POINTER_START
        while (hasValues) {
            val scan = jedis.scan(cursor, param)
            if (scan.result.isNotEmpty()) {
                jedis.mget(*scan.result.toTypedArray()).asSequence()
                    .map { server -> convertFunction.invoke(server) }
                    .forEach { server -> result.add(server) }
            }
            cursor = scan.cursor
            hasValues = cursor != ScanParams.SCAN_POINTER_START
        }
    }
    return result
}
