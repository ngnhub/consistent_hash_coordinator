package com.github.ngnhub.redis_coordinator.utils

import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.model.RedisServerDto

fun RedisServer.toDto() = RedisServerDto(host, port, redistributePageSize, privateHost, privatePort)

fun RedisServerDto.toServer(): RedisServer {
    if (this.privateHost != null && this.privatePort != null) {
        return RedisServer(host, port, privateHost, privatePort, redistributePageSize)
    }
    return RedisServer(host, port, redistributePageSize = redistributePageSize)
}