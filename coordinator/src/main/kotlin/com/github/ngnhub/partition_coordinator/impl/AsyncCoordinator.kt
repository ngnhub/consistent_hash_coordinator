package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.partition_coordinator.Coordinator
import com.github.ngnhub.partition_coordinator.ServerBroker
import kotlinx.coroutines.*

class AsyncCoordinator<K>(
    private val delegated: Coordinator<K>,
    private val serverBroker: ServerBroker<K>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Coordinator<K> by delegated {

    fun startListening() = scope.launch {
        val channel = serverBroker
            .subscribeOnNewServers()
        for (server in channel) {
            delegated.addServer(server)
        }
    }

    fun removeServerAndSendSignal(key: K): Job? {
        val removeServer = delegated.removeServer(key)
        return removeServer?.let {
            scope.launch { serverBroker.sendDownServer(it) }
        }
    }

    fun close() = scope.cancel()
}
