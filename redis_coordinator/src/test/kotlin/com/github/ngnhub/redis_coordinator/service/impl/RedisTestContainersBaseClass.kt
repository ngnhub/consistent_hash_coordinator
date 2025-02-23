package com.github.ngnhub.redis_coordinator.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigInteger

@Testcontainers
@SpringBootTest
class RedisTestContainersBaseClass {

    @Autowired
    protected lateinit var mapper: ObjectMapper

    @MockBean
    protected lateinit var mockedHashFunction: HashFunction<String>

    companion object {

        const val HOST_DOCKER_INTERNAL = "host.docker.internal"

        @Container
        @JvmStatic
        val SERVER_STORAGE = GenericContainer(DockerImageName.parse("redis:7")).withExposedPorts(6379)

        @Container
        @JvmStatic
        val REDIS_1 = createRedisContainer(6090)
        val REDIS_1_HASH = BigInteger.ONE

        @Container
        @JvmStatic
        val REDIS_2 = createRedisContainer(6091)
        val REDIS_2_HASH = BigInteger("5")

        @Container
        @JvmStatic
        val REDIS_3 = createRedisContainer(6092)
        val REDIS_3_HASH = BigInteger("10")

        private fun createRedisContainer(port: Int): GenericContainer<*> {
            val container = GenericContainer(DockerImageName.parse("redis:7"))
                .withExposedPorts(port)
                .withCommand("redis-server", "--port", port.toString())
            return container
        }

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("jedis-server-storage.port") { SERVER_STORAGE.getMappedPort(6379) }
        }

        fun GenericContainer<*>.createRequestDto() = RedisServerDto(
            host,
            firstMappedPort,
            10,
            HOST_DOCKER_INTERNAL,
            firstMappedPort
        )
    }

    @BeforeEach
    fun setUp() {
        // mock hash for servers
        whenever(mockedHashFunction.hash(REDIS_1.host + REDIS_1.firstMappedPort)).thenReturn(REDIS_1_HASH)
        whenever(mockedHashFunction.hash(REDIS_2.host + REDIS_2.firstMappedPort)).thenReturn(REDIS_2_HASH)
        whenever(mockedHashFunction.hash(REDIS_3.host + REDIS_3.firstMappedPort)).thenReturn(REDIS_3_HASH)

        // mock hash for values
        whenever(mockedHashFunction.hash(KEY_1)).thenReturn(BigInteger.ONE)
        whenever(mockedHashFunction.hash(KEY_3)).thenReturn(BigInteger.valueOf(3))
        whenever(mockedHashFunction.hash(KEY_5)).thenReturn(BigInteger.valueOf(5))
        whenever(mockedHashFunction.hash(KEY_7)).thenReturn(BigInteger.valueOf(7))
        whenever(mockedHashFunction.hash(KEY_8)).thenReturn(BigInteger.valueOf(8))
    }
}
