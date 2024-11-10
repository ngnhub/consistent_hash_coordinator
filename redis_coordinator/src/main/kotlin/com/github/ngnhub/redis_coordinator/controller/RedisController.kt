package com.github.ngnhub.redis_coordinator.controller

import com.github.ngnhub.redis_coordinator.service.ServiceStorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController("redis")
class RedisController(val serverStorageService: ServiceStorageService) {

    @GetMapping("/{key}")
    fun getValue(@PathVariable key: String): Any? {
        return serverStorageService.read(key)
    }
}
