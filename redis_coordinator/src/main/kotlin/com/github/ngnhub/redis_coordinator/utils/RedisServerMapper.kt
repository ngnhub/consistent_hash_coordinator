package com.github.ngnhub.redis_coordinator.utils

import com.github.ngnhub.redis_coordinator.model.RedisServer
import com.github.ngnhub.redis_coordinator.model.RedisServerDto

fun RedisServer.toDto() = RedisServerDto(host, port, redistributePageSize)

fun RedisServerDto.toServer() = RedisServer(host, port, redistributePageSize)