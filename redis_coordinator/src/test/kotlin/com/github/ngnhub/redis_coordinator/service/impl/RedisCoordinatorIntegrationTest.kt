package com.github.ngnhub.redis_coordinator.service.impl

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.containers.GenericContainer


@AutoConfigureMockMvc
class RedisCoordinatorIntegrationTest : RedisTestContainersBaseClass() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should add 3 servers and redistribute values`() {
        serverAdded(REDIS_1)
        serverStatusIs(REDIS_1, true)

        addAllValues()

        serverContainsValues(REDIS_1, setOf(VALUE_1, VALUE_3, VALUE_5, VALUE_7, VALUE_8))

        serverAdded(REDIS_2)
        serverStatusIs(REDIS_2, true)
        serverContainsValues(REDIS_1, setOf(VALUE_1, VALUE_7, VALUE_8))
        serverContainsValues(REDIS_2, setOf(VALUE_3, VALUE_5))

        serverAdded(REDIS_3)
        serverStatusIs(REDIS_3, true)
        serverContainsValues(REDIS_1, setOf(VALUE_1))
        serverContainsValues(REDIS_2, setOf(VALUE_3, VALUE_5))
        serverContainsValues(REDIS_3, setOf(VALUE_7, VALUE_8))
    }

    @Test
    fun `should redistribute values when server is removed`() {
        serverAdded(REDIS_1)
        serverAdded(REDIS_2)
        serverAdded(REDIS_3)
        serverStatusIs(REDIS_1, true)
        serverStatusIs(REDIS_2, true)
        serverStatusIs(REDIS_3, true)

        addAllValues()

        serverContainsValues(REDIS_1, setOf(VALUE_1))
        serverContainsValues(REDIS_2, setOf(VALUE_3, VALUE_5))
        serverContainsValues(REDIS_3, setOf(VALUE_7, VALUE_8))

        serverRemoved(REDIS_2)
        serverStatusIs(REDIS_2, false)

        serverContainsValues(REDIS_1, setOf(VALUE_1))
        serverContainsValues(REDIS_3, setOf(VALUE_3, VALUE_5, VALUE_7, VALUE_8))

        serverRemoved(REDIS_3)
        serverStatusIs(REDIS_3, false)
        serverContainsValues(REDIS_1, setOf(VALUE_1, VALUE_3, VALUE_5, VALUE_7, VALUE_8))
    }

    private fun addAllValues() {
        valueAdded(KEY_1, VALUE_1)
        valueAdded(KEY_3, VALUE_3)
        valueAdded(KEY_5, VALUE_5)
        valueAdded(KEY_7, VALUE_7)
        valueAdded(KEY_8, VALUE_8)
    }

    private fun serverAdded(container: GenericContainer<*>) =
        mockMvc.post("/server") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(container.createRequestDto())
        }.andExpect {
            status { is2xxSuccessful() }
        }

    private fun serverRemoved(container: GenericContainer<*>) =
        mockMvc.delete("/server/{host}/{port}", container.host, container.firstMappedPort).andExpect {
            status { is2xxSuccessful() }
        }

    private fun valueAdded(key: String, value: String) =
        mockMvc.post("/redis/{key}", key) {
            content = value
        }.andExpect {
            status { is2xxSuccessful() }
        }

    private fun serverStatusIs(container: GenericContainer<*>, status: Boolean) =
        mockMvc.get("/server/{host}/{port}", container.host, container.firstMappedPort)
            .andExpect {
                status { is2xxSuccessful() }
                content { string(status.toString()) }
            }

    private fun serverContainsValues(container: GenericContainer<*>, values: Set<String>) =
        mockMvc.get("/redis/${container.host + container.firstMappedPort}/all")
            .andExpect {
                content { json(mapper.writeValueAsString(values)) }
            }
}
