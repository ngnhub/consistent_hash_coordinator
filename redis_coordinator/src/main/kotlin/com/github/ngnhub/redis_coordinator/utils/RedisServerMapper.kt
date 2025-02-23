package com.github.ngnhub.redis_coordinator.utils

import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.model.VPCRedisServer
import com.github.ngnhub.redis_coordinator.model.VPCRedisServerDto

fun RedisServer.toDto() = RedisServerDto(host, port, redistributePageSize)

fun RedisServerDto.toServer() = RedisServer(host, port, redistributePageSize)

fun VPCRedisServerDto.toServer() =
    VPCRedisServer(host, port, redistributePageSize, privateHost = privateHost, privatePort = privatePort)