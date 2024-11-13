package com.github.ngnhub.redis_coordinator.controller

import com.github.ngnhub.redis_coordinator.service.RedisOperationsService
import org.springframework.web.bind.annotation.*

@RestController("redis")
class RedisController(val redisOperationsService: RedisOperationsService) {

    @GetMapping("/{key}")
    fun getValue(@PathVariable key: String): Any? {
        return redisOperationsService[key]
    }

    @PostMapping("/{key}")
    fun addValue(@PathVariable key: String, @RequestBody body: String) {
        redisOperationsService[key] = body
    }
}
