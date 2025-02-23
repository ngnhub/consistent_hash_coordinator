package com.github.ngnhub.redis_coordinator.model

data class VPCRedisServerDto(
    val host: String,
    val port: Int,
    val redistributePageSize: Int,
    val privateHost: String,
    val privatePort: Int
)
