package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.consistent_hash.ConsistentHashMap
import com.github.ngnhub.consistent_hash.impl.MurmurHashFunction
import com.github.ngnhub.partition_coordinator.Coordinator
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.ServerBroker
import com.github.ngnhub.partition_coordinator.StorageProvider
import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class DefaultCoordinator(
    private val consistentHashMap: ConsistentHashMap<String, Server<String>> = ConsistentHashMap(MurmurHashFunction()), // todo: IP
    private val storageProvider: StorageProvider<String>,
    private val serverBroker: ServerBroker<String>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : Coordinator<String> {

    init {
        scope.launch { onStart() }
    }

    private suspend fun onStart() {
        serverBroker.subscribeOnNewServers().consumeEach { addServer(it) }
    }

    override fun addServer(server: Server<String>) {
        consistentHashMap[server.key] = server
        val nextServer = findFirstAvailableWithUnhealthyRemoval(server.key)
        storageProvider.reDistribute(nextServer, server)
    }

    override fun addVirtualNodes(vararg virtualNodes: String, sourceNode: String) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: Any) {
        val server = findFirstAvailableWithUnhealthyRemoval(key)
        storageProvider.insert(key, server)
    }

    override fun get(key: String): Any? {
        val server = findFirstAvailableWithUnhealthyRemoval(key)
        return storageProvider.read(key, server)
    }

    private fun findFirstAvailableWithUnhealthyRemoval(key: String): Server<String> {
        val server = consistentHashMap[key] ?: throw NoAvailableSever()
        if (server.health()) {
            return server
        }
        consistentHashMap - server.key
        serverBroker.sendDownServer(server)
        return findFirstAvailableWithUnhealthyRemoval(server.key)
    }

    fun tearDown() {
        serverBroker.close()
        scope.cancel()
    }
}
