package com.github.ngnhub.redis_coordinator.service

import com.github.ngnhub.redis_coordinator.model.RedisServer

interface ServerStorageService {

    fun addServer(host: String, port: Int)

    operator fun get(key: String): RedisServer

    operator fun minus(key: String)

    fun isAlive(host: String, port: Int): Boolean
}
