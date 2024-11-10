package com.github.ngnhub.redis_coordinator.config

import com.github.ngnhub.partition_coordinator.impl.DefaultCoordinator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoordinatorConfig {

    @Bean
    fun coordinator() = DefaultCoordinator()
}
