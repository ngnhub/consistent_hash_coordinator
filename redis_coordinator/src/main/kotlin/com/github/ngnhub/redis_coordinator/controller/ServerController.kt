package com.github.ngnhub.redis_coordinator.controller

import com.github.ngnhub.redis_coordinator.service.ServerSearchService
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.service.impl.JedisServerStorage
import org.springframework.web.bind.annotation.*

@RestController
class ServerController(val serverSearchService: ServerSearchService, val serverStorage: JedisServerStorage) {

    @PostMapping("/server")
    fun addServer(@RequestBody server: RedisServerDto) = serverSearchService.addServer(server)

    @GetMapping("/server/{host}/{port}")
    fun isAlive(@PathVariable host: String, @PathVariable port: Int) = serverSearchService.isAlive(host, port)

    @GetMapping("/servers")
    fun getAll() = serverStorage.getAll()

    @DeleteMapping("/server/{host}/{port}")
    fun remove(@PathVariable host: String, @PathVariable port: Int) = serverSearchService - (host + port)
}
