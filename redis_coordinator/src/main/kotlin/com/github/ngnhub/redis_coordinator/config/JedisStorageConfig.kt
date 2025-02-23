package com.github.ngnhub.redis_coordinator.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.model.VPCRedisServerDto
import com.github.ngnhub.redis_coordinator.service.impl.JedisServerStorage
import com.github.ngnhub.redis_coordinator.utils.DEFAULT_PROFILE
import com.github.ngnhub.redis_coordinator.utils.VPC_PROFILE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class JedisStorageConfig {

    @Bean
    @Profile(DEFAULT_PROFILE)
    fun jedisStorage() =
        JedisServerStorage<RedisServerDto>(JedisServerStorageProperty(), ObjectMapper().findAndRegisterModules())

    @Bean
    @Profile(VPC_PROFILE)
    fun vpcJedisStorage() =
        JedisServerStorage<VPCRedisServerDto>(JedisServerStorageProperty(), ObjectMapper().findAndRegisterModules())
}
