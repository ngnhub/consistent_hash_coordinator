package com.github.ngnhub.redis_coordinator.controller

import com.github.ngnhub.redis_coordinator.service.RedisOperationsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/redis")
class RedisController(val redisOperationsService: RedisOperationsService) {

    @GetMapping("/{key}")
    fun getValue(@PathVariable key: String): Any? {
        return redisOperationsService[key]
    }

    @GetMapping("/{server}/all")
    fun getAllValues(@PathVariable server: String): Any? {
        return redisOperationsService.getAll(server)
    }

    @PostMapping("/{key}")
    fun addValue(@PathVariable key: String, @RequestBody value: String) {
        redisOperationsService[key] = value
    }
}
