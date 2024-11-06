package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.consistent_hash.ConsistentHashMap
import com.github.ngnhub.partition_coordinator.Coordinator
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.StorageProvider
import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever

class DefaultCoordinator(
    private val downServerStorage: Set<Server<String>>,
    private val consistentHashMap: ConsistentHashMap<String, Server<String>>, // todo: IP
    private val storageProvider: StorageProvider<String>
) : Coordinator<String> {

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
        downServerStorage + server
        return findFirstAvailableWithUnhealthyRemoval(server.key)
    }
}
