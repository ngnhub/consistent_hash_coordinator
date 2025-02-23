package com.github.ngnhub.redis_coordinator.service.impl

import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.GenericContainer
import java.math.BigInteger

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@AutoConfigureMockMvc
class RedisCoordinatorIntegrationTest : RedisTestContainersBaseClass() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should add 3 servers and redistribute values`() {
        serverAdded(REDIS_1)
        serverIsAlive(REDIS_1)

        valueAdded(KEY_1, VALUE_1)
        valueAdded(KEY_3, VALUE_3)
        valueAdded(KEY_5, VALUE_5)
        valueAdded(KEY_7, VALUE_7)
        valueAdded(KEY_8, VALUE_8)

        serverContainsValues(REDIS_1, setOf(VALUE_1, VALUE_3, VALUE_5, VALUE_7, VALUE_8))

        serverAdded(REDIS_2)
        serverIsAlive(REDIS_2)
        serverContainsValues(REDIS_1, setOf(VALUE_1, VALUE_7, VALUE_8))
        serverContainsValues(REDIS_2, setOf(VALUE_3, VALUE_5))

        serverAdded(REDIS_3)
        serverIsAlive(REDIS_3)
        serverContainsValues(REDIS_1, setOf(VALUE_1))
        serverContainsValues(REDIS_2, setOf(VALUE_3, VALUE_5))
        serverContainsValues(REDIS_3, setOf(VALUE_7, VALUE_8))
    }

    @Test
    fun `should redistribute values when server is removed`() {
        TODO("Not yet implemented")
    }

    private fun serverAdded(container: GenericContainer<*>) =
        mockMvc.post("/server") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(container.createRequestDto())
        }.andExpect {
            status { is2xxSuccessful() }
        }

    private fun valueAdded(key: String, value: String) =
        mockMvc.post("/redis/{key}", key) {
            content = value
        }.andExpect {
            status { is2xxSuccessful() }
        }

    private fun serverIsAlive(container: GenericContainer<*>) =
        mockMvc.get("/server/{host}/{port}", container.host, container.firstMappedPort)
            .andExpect {
                status { is2xxSuccessful() }
                content { string("true") }
            }

    private fun serverContainsValues(container: GenericContainer<*>, values: Set<String>) =
        mockMvc.get("/redis/${container.host + container.firstMappedPort}/all")
            .andExpect {
                content { json(mapper.writeValueAsString(values)) }
            }
}
