package com.github.ngnhub.redis_coordinator.service

interface RedisOperationsService {

    operator fun get(key: String): Any?

    operator fun set(key: String, value: Any?)
}
