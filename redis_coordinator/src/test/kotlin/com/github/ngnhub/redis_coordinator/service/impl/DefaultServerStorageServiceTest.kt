package com.github.ngnhub.redis_coordinator.service.impl

import com.github.ngnhub.redis_coordinator.service.StorableRedisServer
import com.github.ngnhub.redis_coordinator.service.impl.config.TestRedisEmbeddedConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class, SpringExtension::class)
@SpringBootTest(classes = [TestRedisEmbeddedConfig::class])
internal class JedisServerStorageTest {

    @Autowired
    lateinit var jedisStorageServer: JedisServerStorage

    @AfterEach
    fun tearDown() {
        jedisStorageServer.getAll().forEach { jedisStorageServer - (it.host + it.port) }
    }

    @Test
    fun `should add and return server`() {
        // given
        val input = StorableRedisServer("host", 0, 0)
        val key = "key"

        // when
        jedisStorageServer[key] = input

        // then
        val actual = jedisStorageServer[key]
        assertEquals(input, actual)
    }

    @Test
    fun `should add than remove value`() {
        // given
        val input = StorableRedisServer("host", 0, 0)
        val key = "key"
        jedisStorageServer[key] = input
        val returned = jedisStorageServer[key]
        assertEquals(input, returned)

        // when
        jedisStorageServer - key

        // then
        assertNull(jedisStorageServer[key])
    }

    @Test
    fun `should return all`() {
        // given
        val input1 = StorableRedisServer("host1", 0, 0)
        val input2 = StorableRedisServer("host2", 0, 0)

        // when
        jedisStorageServer["key1"] = input1
        jedisStorageServer["key2"] = input2

        // then
        val actual = jedisStorageServer.getAll()
        assertEquals(2, actual.size)
        assertTrue(actual.contains(input1))
        assertTrue(actual.contains(input2))
    }
}
