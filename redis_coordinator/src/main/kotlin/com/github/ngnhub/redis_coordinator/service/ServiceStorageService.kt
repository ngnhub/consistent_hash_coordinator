package com.github.ngnhub.redis_coordinator.service

interface ServiceStorageService {

    fun addServer(host: String, port: Int)

    fun read(key: String): String // todo  move to a more specific service

    fun isAlive(host: String, port: Int): Boolean
}
