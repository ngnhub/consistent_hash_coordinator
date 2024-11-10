package com.github.ngnhub.redis_coordinator.controller

import com.github.ngnhub.redis_coordinator.service.ServiceStorageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ServerController(val serverStorageService: ServiceStorageService) {

    @PostMapping("/server/{host}/{port}")
    fun addServer(@PathVariable host: String, @PathVariable port: Int) {
        serverStorageService.addServer(host, port)
    }

    @GetMapping("/server/{host}/{port}")
    fun isAlive(@PathVariable host: String, @PathVariable port: Int): Boolean {
        return serverStorageService.isAlive(host, port)
    }
}
