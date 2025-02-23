package com.github.ngnhub.redis_coordinator.controller

import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.service.impl.JedisServerStorage
import org.springframework.web.bind.annotation.*

@RestController
class ServerController(val serverStorageService: ServerStorageService, val serverStorage: JedisServerStorage) {

    @PostMapping("/server")
    fun addServer(@RequestBody server: RedisServerDto) = serverStorageService.addServer(server)

    @GetMapping("/server/{host}/{port}")
    fun isAlive(@PathVariable host: String, @PathVariable port: Int) = serverStorageService.isAlive(host, port)

    @GetMapping("/servers")
    fun getAll() = serverStorage.getAll()

    @DeleteMapping("/server/{host}/{port}")
    fun remove(@PathVariable host: String, @PathVariable port: Int) = serverStorageService - (host + port)
}
