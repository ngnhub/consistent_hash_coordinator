package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.consistent_hash.ConsistentHashRing
import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.kotlin.*
import java.math.BigInteger
import kotlin.test.assertEquals


class DefaultCoordinatorTest {

    @Mock
    lateinit var consistentHashFunction: HashFunction<String>

    @Spy
    val consistentHashRing = ConsistentHashRing<Server>()

    private lateinit var coordinator: DefaultCoordinator<Server>

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        coordinator = DefaultCoordinator(consistentHashFunction, consistentHashRing)
    }

    @Test
    fun `should add a server and redistribute values`() {
        // given
        val key1 = "server1"
        val server1 = mock<Server> {
            on(it.key) doReturn key1
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.ONE
        }
        val key2 = "server2"
        val server2 = mock<Server> {
            on(it.key) doReturn key2
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.TWO
        }
        val key3 = "server3"
        val server3 = mock<Server> {
            on(it.key) doReturn key3
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.valueOf(3L)
        }
        whenever(consistentHashFunction.hash(key1)).thenReturn(BigInteger.valueOf(1))
        whenever(consistentHashFunction.hash(key2)).thenReturn(BigInteger.valueOf(2))
        whenever(consistentHashFunction.hash(key3)).thenReturn(BigInteger.valueOf(3))

        // when
        coordinator + server3
        coordinator + server2
        coordinator + server1

        // then
        verify(consistentHashRing)[BigInteger.valueOf(1)] = server1
        verify(consistentHashRing)[BigInteger.valueOf(2)] = server2
        verify(consistentHashRing)[BigInteger.valueOf(3)] = server3
        verify(server2).reDistribute(server3, consistentHashFunction)
        verify(server1).reDistribute(server2, consistentHashFunction)
    }

    @Test
    fun `should not redistribute if a single server`() {
        // given
        val key1 = "server1"
        val server1 = mock<Server> {
            on(it.key) doReturn key1
            on(it.health()) doReturn true
        }
        whenever(consistentHashFunction.hash(key1)).thenReturn(BigInteger.ONE)

        // when
        coordinator + server1

        // then
        verify(consistentHashRing)[BigInteger.valueOf(1)] = server1
        verify(server1, never()).reDistribute(anyOrNull(), anyOrNull())
    }

    @Test
    fun `should not redistribute if server is not healthy`() {
        // given
        val key1 = "server1"
        val server1 = mock<Server> {
            on(it.key) doReturn key1
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.ONE
        }
        val key2 = "server2"
        val server2 = mock<Server> {
            on(it.key) doReturn key2
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.TWO
        }
        whenever(consistentHashFunction.hash(key1)).thenReturn(BigInteger.ONE)
        whenever(consistentHashFunction.hash(key2)).thenReturn(BigInteger.TWO)

        // when
        coordinator + server2
        assertEquals(1, coordinator.serversCount)
        whenever(server2.health()).thenReturn(false)
        coordinator + server1
        assertEquals(1, coordinator.serversCount)

        // then
        verify(consistentHashRing)[BigInteger.valueOf(2)] = server2
        verify(consistentHashRing) - BigInteger.valueOf(2)
        verify(consistentHashRing)[BigInteger.valueOf(1)] = server1
        verify(server1, never()).reDistribute(anyOrNull(), anyOrNull())
    }

    @Test
    fun `should return server that is closest to the key`() {
        // given
        val key1 = "key1"
        val server1 = mock<Server> {
            on(it.key) doReturn key1
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.ONE
        }
        val key3 = "key3"
        val server3 = mock<Server> {
            on(it.key) doReturn key3
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.valueOf(3)
        }
        whenever(consistentHashFunction.hash(key1)).thenReturn(BigInteger.ONE)
        whenever(consistentHashFunction.hash(key3)).thenReturn(BigInteger.valueOf(3))
        coordinator + server1
        coordinator + server3
        val key2 = "key2"
        whenever(consistentHashFunction.hash(key2)).thenReturn(BigInteger.TWO)

        // when
        val actual = coordinator[key2]

        // then
        assertEquals(server3, actual)
    }

    @Test
    fun `should throw if no available server exist while reading`() {
        // given
        val key = "key"
        whenever(consistentHashFunction.hash(key)).thenReturn(BigInteger.ONE)

        // when
        assertThrows<NoAvailableSever> { coordinator[key] }
    }

    @Test
    fun `should remove from consistent hash`() {
        // given
        val key = "key"
        val server1 = mock<Server> {
            on(it.key) doReturn key
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.ONE
        }
        whenever(consistentHashFunction.hash(key)).thenReturn(BigInteger.ONE)
        coordinator + server1
        assertEquals(1, coordinator.serversCount)

        // when
        coordinator - key

        // then
        verify(consistentHashRing) - BigInteger.ONE
        assertEquals(0, coordinator.serversCount)
    }

    @Test
    fun `should return size base on consistent hash size`() {
        // given
        val key1 = "server1"
        val server1 = mock<Server> {
            on(it.key) doReturn key1
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.ONE
        }
        val key2 = "server2"
        val server2 = mock<Server> {
            on(it.key) doReturn key2
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.TWO
        }
        val key3 = "server3"
        val server3 = mock<Server> {
            on(it.key) doReturn key3
            on(it.health()) doReturn true
            on(it.hash) doReturn BigInteger.valueOf(3L)
        }
        whenever(consistentHashFunction.hash(key1)).thenReturn(BigInteger.valueOf(1))
        whenever(consistentHashFunction.hash(key2)).thenReturn(BigInteger.valueOf(2))
        whenever(consistentHashFunction.hash(key3)).thenReturn(BigInteger.valueOf(3))

        // when
        val empty = coordinator.serversCount
        coordinator + server1
        val singleElem = coordinator.serversCount
        coordinator + server2
        val twoElems = coordinator.serversCount
        coordinator + server3
        val threeElems = coordinator.serversCount

        // then
        assertEquals(0, empty)
        assertEquals(1, singleElem)
        assertEquals(2, twoElems)
        assertEquals(3, threeElems)
    }
}
