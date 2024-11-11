package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.consistent_hash.ConsistentHashMap
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import kotlin.test.assertEquals


class DefaultCoordinatorTest {

    @Mock
    lateinit var consistentHashMap: ConsistentHashMap<String, Server>

    private lateinit var coordinator: DefaultCoordinator<Server>

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        coordinator = DefaultCoordinator(consistentHashMap)
    }

    @Test
    fun `should add a server and redistribute values`() {
        // given
        val key1 = "server1"
        val server1 = mock<Server> {
            on(it.key) doReturn key1
            on(it.health()) doReturn true
        }
        val key2 = "server2"
        val server2 = mock<Server> {
            on(it.key) doReturn key2
            on(it.health()) doReturn true
        }
        val key3 = "server3"
        val server3 = mock<Server> {
            on(it.key) doReturn key3
            on(it.health()) doReturn true
        }
        whenever(consistentHashMap[server1.key]).thenReturn(server1)
        whenever(consistentHashMap[server2.key]).thenReturn(server2)
        whenever(consistentHashMap[server3.key]).thenReturn(server3)
        whenever(consistentHashMap.nextAfter(key1)).thenReturn(server2)
        whenever(consistentHashMap.nextAfter(key2)).thenReturn(server3)
        whenever(consistentHashMap.nextAfter(key3)).thenReturn(server1)

        // when
        coordinator + server3
        coordinator + server2
        coordinator + server1

        // then
        verify(consistentHashMap)[key1] = server1
        verify(consistentHashMap)[key2] = server2
        verify(consistentHashMap)[key3] = server3
        verify(server1).reDistribute(server2)
        verify(server2).reDistribute(server3)
        verify(server3).reDistribute(server1)
    }

    @Test
    fun `should not redistribute if a single server`() {
        // given
        val key1 = "server1"
        val server1 = mock<Server> {
            on(it.key) doReturn key1
            on(it.health()) doReturn true
        }
        whenever(consistentHashMap.nextAfter(key1)).thenReturn(null)

        // when
        coordinator + server1

        // then
        verify(consistentHashMap)[key1] = server1
        verify(server1, never()).reDistribute(anyOrNull())
    }

    @Test
    fun `should not redistribute if server is not healthy`() {
        // given
        val key1 = "server1"
        val server1 = mock<Server> {
            on(it.key) doReturn key1
            on(it.health()) doReturn true
        }
        val key2 = "server2"
        val server2 = mock<Server> {
            on(it.key) doReturn key2
            on(it.health()) doReturn false
        }
        whenever(consistentHashMap.nextAfter(key1)).thenReturn(server2)
        whenever(consistentHashMap[key2]).thenReturn(server2).thenReturn(null)

        // when
        coordinator + server1

        // then
        verify(consistentHashMap)[key1] = server1
        verify(consistentHashMap) - key2
        verify(server1, never()).reDistribute(anyOrNull())
    }

    @Test
    fun `should return server that is closest to the key`() {
        // given
        val key = "key"
        val server1 = mock<Server> {
            on(it.key) doReturn key
            on(it.health()) doReturn true
        }
        val expected = "values"
        whenever(consistentHashMap[key]).thenReturn(server1)

        // when
        val actual = coordinator[key]

        // then
        assertEquals(server1, actual)
    }

    @Test
    fun `should throw if no available server exist while reading`() {
        // given
        val key = "key"
        whenever(consistentHashMap[key]).thenReturn(null)

        // when
        assertThrows<NoAvailableSever> { coordinator[key] }
    }

    @Test
    fun `should remove from consistent hash`() {
        // given
        val key = "key"
        coordinator.removeServer(key)

        // then
        verify(consistentHashMap) - key
    }

    @Test
    fun `should return size base on consistent hash size`() {
        // given
        whenever(consistentHashMap.size)
            .thenReturn(1)
            .thenReturn(5)
            .thenReturn(3)

        // when
        val size1 = coordinator.serversCount
        val size2 = coordinator.serversCount
        val size3 = coordinator.serversCount

        // then
        assertEquals(1, size1)
        assertEquals(5, size2)
        assertEquals(3, size3)
    }
}
