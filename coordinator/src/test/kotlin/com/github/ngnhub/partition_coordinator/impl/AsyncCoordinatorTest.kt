package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.ServerBroker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*


class AsyncCoordinatorTest {

    @Mock
    private lateinit var broker: ServerBroker<Server>

    private lateinit var delegated: DefaultCoordinator<Server>

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        delegated = DefaultCoordinator()
    }

    @Test
    fun `should collect 3 servers asynchronously`() = runTest {
        // given
        val channel = Channel<Server>()
        whenever(broker.subscribeOnNewServers()).thenReturn(channel)
        val coordinator = AsyncCoordinator(delegated, broker)

        // when
        val job = coordinator.startListening()
        sendThreeMessages(channel)
        channel.close()

        // verify
        job.join()
        assertEquals(3, coordinator.serversCount)
    }

    @Test
    fun `should remove 2 servers and 1 should left`() = runTest {
        // given
        val channel = Channel<Server>()
        whenever(broker.subscribeOnNewServers()).thenReturn(channel)
        val coordinator = AsyncCoordinator(delegated, broker)

        // when
        val consumeJob = coordinator.startListening()
        sendThreeMessages(channel)
        channel.close()
        consumeJob.join()
        coordinator - "server1"
        coordinator - "server2"
        delay(100L)

        // verify
        verify(broker, times(2)).sendDownServer(anyOrNull())
        assertEquals(1, coordinator.serversCount)
    }

    @Test
    fun `should remove without sending signal if value is not found`() = runTest {
        // given
        val channel = Channel<Server>()
        whenever(broker.subscribeOnNewServers()).thenReturn(channel)
        val coordinator = AsyncCoordinator(delegated, broker)

        // when
        val consumeJob = coordinator.startListening()
        sendThreeMessages(channel)
        channel.close()
        consumeJob.join()
        val sendingJob = coordinator - "server4"
        delay(100L)

        // verify
        assertNull(sendingJob)
        verify(broker, never()).sendDownServer(anyOrNull())
        assertEquals(3, coordinator.serversCount)
    }

    @Test
    fun `should close scope when close func is invoked`() = runTest {
        // given
        val channel = Channel<Server>()
        whenever(broker.subscribeOnNewServers()).thenReturn(channel)
        val scope = spy(CoroutineScope(Dispatchers.Default))
        val coordinator = AsyncCoordinator(delegated, broker, scope)

        // when
        coordinator.startListening()
        sendThreeMessages(channel)
        channel.close()
        assertTrue(scope.isActive)
        coordinator.close()

        // verify
        assertFalse(scope.isActive)
        assertEquals(3, coordinator.serversCount)
    }

    private suspend fun sendThreeMessages(channel: Channel<Server>) {
        val server1 = mock<Server>()
        val server2 = mock<Server>()
        val server3 = mock<Server>()
        whenever(server1.key).thenReturn("server1")
        whenever(server1.health()).thenReturn(true)
        whenever(server2.key).thenReturn("server2")
        whenever(server2.health()).thenReturn(true)
        whenever(server3.key).thenReturn("server3")
        whenever(server3.health()).thenReturn(true)
        channel.send(server1)
        channel.send(server2)
        channel.send(server3)
    }
}
