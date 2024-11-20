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
            delegated + server
        }
    }

    override fun minus(server: S): S? {
        val removed = delegated - server
        removed?.let {
            serverBroker.sendDownServer(it)
        }
        return removed
    }

    fun close() = scope.cancel()
}
