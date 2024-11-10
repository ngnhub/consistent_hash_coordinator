package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.partition_coordinator.Coordinator
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.ServerBroker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AsyncCoordinator<K, S : Server>(
    private val delegated: Coordinator<K, S>,
    private val serverBroker: ServerBroker<S>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Coordinator<K, S> by delegated {

    fun startListening() = scope.launch {
        val channel = serverBroker
            .subscribeOnNewServers()
        for (server in channel) {
            delegated.addServer(server)
        }
    }

    override fun removeServer(key: K): S? {
        val removeServer = delegated.removeServer(key)
        removeServer?.let {
            scope.launch { serverBroker.sendDownServer(it) }
        }
        return removeServer
    }

    fun close() = scope.cancel()
}
