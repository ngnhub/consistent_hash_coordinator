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

    override fun plus(server: S) {
        consistentHashMap[server.key] = server
        consistentHashMap.nextAfter(server.key)
            ?.let { nextServer ->
                findFirstAvailableWithUnhealthyRemoval(nextServer.key)
                    ?.let { nextAvailableServer -> server.reDistribute(nextAvailableServer, consistentHashMap.hashFunction) }
            }
    }

    override fun get(key: String): S {
        return findFirstAvailableWithUnhealthyRemoval(key) ?: throw NoAvailableSever()
    }

    private fun findFirstAvailableWithUnhealthyRemoval(key: String): S? {
        val server = consistentHashMap[key] ?: return null
        if (server.health()) {
            return server
        }
        removeServer(server.key)
        return findFirstAvailableWithUnhealthyRemoval(server.key)
    }

    override fun addVirtualNodes(vararg virtualNodes: S, sourceNode: S) {
        TODO("Not yet implemented")
    }

    override fun removeServer(key: String): S? {
        return consistentHashMap - key
    }
}
