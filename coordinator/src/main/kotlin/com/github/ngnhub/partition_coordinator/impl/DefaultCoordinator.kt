package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.consistent_hash.ConsistentHashMap
import com.github.ngnhub.consistent_hash.impl.MurmurHashFunction
import com.github.ngnhub.partition_coordinator.Coordinator
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.StorageProvider
import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever

class DefaultCoordinator(
    private val consistentHashMap: ConsistentHashMap<String, Server<String>> = ConsistentHashMap(MurmurHashFunction()), // todo: IP
    private val storageProvider: StorageProvider<String>
) : Coordinator<String> {

    override val serversCount: Int
        get() = consistentHashMap.size

    override fun addServer(server: Server<String>) {
        consistentHashMap[server.key] = server
        consistentHashMap.nextAfter(server.key)
            ?.let { nextServer ->
                findFirstAvailableWithUnhealthyRemoval(nextServer.key)
                    ?.let { nextAvailableServer -> storageProvider.reDistribute(nextAvailableServer, server) }
            }
    }

    override fun addVirtualNodes(vararg virtualNodes: String, sourceNode: String) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: Any) {
        val server = findFirstAvailableWithUnhealthyRemoval(key) ?: throw NoAvailableSever()
        storageProvider.insert(key, server)
    }

    override fun get(key: String): Any? {
        val server = findFirstAvailableWithUnhealthyRemoval(key) ?: throw NoAvailableSever()
        return storageProvider.read(key, server)
    }

    private fun findFirstAvailableWithUnhealthyRemoval(key: String): Server<String>? {
        val server = consistentHashMap[key] ?: return null
        if (server.health()) {
            return server
        }
        removeServer(server.key)
        return findFirstAvailableWithUnhealthyRemoval(server.key)
    }

    override fun removeServer(key: String): Server<String>? {
        return consistentHashMap - key
    }
}
