package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.redis_coordinator.model.RedisServerDto
import com.github.ngnhub.redis_coordinator.service.RedisOperationsService
import com.github.ngnhub.redis_coordinator.service.ServerStorageService
import com.github.ngnhub.redis_coordinator.service.impl.config.TestRedisOperationConfig
import com.github.ngnhub.redis_coordinator.utils.readAll
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigInteger

@ExtendWith(SpringExtension::class)
@Import(TestRedisOperationConfig::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class DefaultRedisOperationsServiceTest {

    @Autowired
    private lateinit var mockedHashFunction: HashFunction<String>

    @Autowired
    private lateinit var redisOperationsService: RedisOperationsService

    @Autowired
    private lateinit var serverStorageService: ServerStorageService


    companion object {
        private val serverStorage = redis.embedded.RedisServer(6000)
        private val serv1 = redis.embedded.RedisServer(6001)
        private val serv2 = redis.embedded.RedisServer(6002)
        private val serv3 = redis.embedded.RedisServer(6003)

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            serverStorage.start()
            serv1.start()
            serv2.start()
            serv3.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            serverStorage.stop()
            serv1.stop()
            serv2.stop()
            serv3.stop()
        }
    }

//    @BeforeEach
//    fun setUp() {
//        TODO("Not yet implemented")
//    }

    @Test
    fun `should add 3 servers with redistribution`() {
        // given
        whenever(mockedHashFunction.hash("localhost6001")).thenReturn(BigInteger.TWO)
        whenever(mockedHashFunction.hash("localhost6002")).thenReturn(BigInteger.valueOf(5))
        whenever(mockedHashFunction.hash("localhost6003")).thenReturn(BigInteger.valueOf(10))
        val key1 = "key1"
        val key3 = "key3"
        val key5 = "key5"
        val key7 = "key7"
        val key8 = "key8"
        whenever(mockedHashFunction.hash(key1)).thenReturn(BigInteger.ONE)
        whenever(mockedHashFunction.hash(key3)).thenReturn(BigInteger.valueOf(3))
        whenever(mockedHashFunction.hash(key5)).thenReturn(BigInteger.valueOf(5))
        whenever(mockedHashFunction.hash(key7)).thenReturn(BigInteger.valueOf(7))
        whenever(mockedHashFunction.hash(key8)).thenReturn(BigInteger.valueOf(8))

        serverStorageService.addServer(RedisServerDto("localhost", 6001, 10))
        val value1 = "value1"
        val value3 = "value3"
        val value5 = "value5"
        val value7 = "value7"
        val value8 = "value8"
        redisOperationsService[key1] = value1
        redisOperationsService[key3] = value3
        redisOperationsService[key5] = value5
        redisOperationsService[key7] = value7
        redisOperationsService[key8] = value8

        val server1 = serverStorageService["localhost6001"]
        assertNotNull(server1)

        val values = readAll(server1.redisPool, 10) { it }
        assertEquals(listOf(value1, value3, value5, value7, value8).toSet(), values.toSet())

    }

    @Test
    fun get() {
    }

    @Test
    fun minus() {
    }

    @Test
    fun isAlive() {
    }
}