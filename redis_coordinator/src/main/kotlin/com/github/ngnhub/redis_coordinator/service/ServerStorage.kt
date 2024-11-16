package com.github.ngnhub.redis_coordinator.service

interface ServerStorage {

    fun getAll(): List<StorableRedisServer>

    operator fun set(key: String, server: StorableRedisServer)

    operator fun minus(key: String)

    operator fun get(key: String): StorableRedisServer?
}

data class StorableRedisServer(val host: String, val port: Int, val redistributePageSize: Int)
