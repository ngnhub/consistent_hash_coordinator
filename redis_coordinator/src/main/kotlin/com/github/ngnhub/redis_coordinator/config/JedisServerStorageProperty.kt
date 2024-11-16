package com.github.ngnhub.redis_coordinator.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jedis-server-storage")
data class JedisServerStorageProperty(val host: String = "", val port: Int = -1)
