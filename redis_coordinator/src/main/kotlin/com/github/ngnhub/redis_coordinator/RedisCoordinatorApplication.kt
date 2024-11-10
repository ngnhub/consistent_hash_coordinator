package com.github.ngnhub.redis_coordinator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedisCoordinatorApplication

fun main(args: Array<String>) {
    runApplication<RedisCoordinatorApplication>(*args)
}
