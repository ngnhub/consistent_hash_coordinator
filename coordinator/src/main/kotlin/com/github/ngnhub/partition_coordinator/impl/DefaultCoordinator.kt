package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.consistent_hash.ConsistentHashMap
import com.github.ngnhub.consistent_hash.impl.MurmurHashFunction
import com.github.ngnhub.partition_coordinator.Coordinator
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever

class DefaultCoordinator<S : Server>(
    private val consistentHashMap: ConsistentHashMap<String, S> = ConsistentHashMap(MurmurHashFunction())
) : Coordinator<String, S> {

    override val serversCount: Int
        get() = consistentHashMap.size

    override fun addServer(server: S) {
        consistentHashMap[server.key] = server
        consistentHashMap.nextAfter(server.key)
            ?.let { nextServer ->
                findFirstAvailableWithUnhealthyRemoval(nextServer.key)
                    ?.let { nextAvailableServer -> server.reDistribute(nextAvailableServer) }
            }
    }

    override fun addVirtualNodes(vararg virtualNodes: S, sourceNode: S) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: Any) {
        val server = findFirstAvailableWithUnhealthyRemoval(key) ?: throw NoAvailableSever()
        server.insert(key, value)
    }

    override fun get(key: String): Any? {
        val server = findFirstAvailableWithUnhealthyRemoval(key) ?: throw NoAvailableSever()
        return server.read(key)
    }

    private fun findFirstAvailableWithUnhealthyRemoval(key: String): Server? {
        val server = consistentHashMap[key] ?: return null
        if (server.health()) {
            return server
        }
        removeServer(server.key)
        return findFirstAvailableWithUnhealthyRemoval(server.key)
    }

    override fun removeServer(key: String): S? {
        return consistentHashMap - key
    }
}
