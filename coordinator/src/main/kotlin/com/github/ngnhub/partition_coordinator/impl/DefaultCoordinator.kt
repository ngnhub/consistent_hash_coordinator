package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.consistent_hash.ConsistentHashRing
import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.consistent_hash.impl.MurmurHashFunction
import com.github.ngnhub.partition_coordinator.Coordinator
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever
import java.math.BigInteger

class DefaultCoordinator<S : Server>(
    private val hashFunction: HashFunction<String> = MurmurHashFunction(),
) : Coordinator<String, S> {


    private val consistentHashRing: ConsistentHashRing<S> = ConsistentHashRing()

    override val serversCount: Int
        get() = consistentHashRing.size

    override fun plus(server: S) {
        val newNodeHash = hashFunction.hash(server.key)
        server.hash = newNodeHash
        consistentHashRing[newNodeHash] = server
        consistentHashRing.nextAfter(newNodeHash)
            ?.let { nextServer ->
                val nextServerHash = hashFunction.hash(nextServer.key)
                findFirstAvailableWithUnhealthyRemoval(nextServerHash)
                    ?.let { nextAvailableServer ->
                        server.reDistribute(nextAvailableServer, hashFunction)
                    }
            }
    }

    override fun get(key: String): S {
        val hash = hashFunction.hash(key)
        return findFirstAvailableWithUnhealthyRemoval(hash) ?: throw NoAvailableSever()
    }

    private fun findFirstAvailableWithUnhealthyRemoval(hash: BigInteger): S? {
        val server = consistentHashRing[hash] ?: return null
        if (server.health()) {
            return server
        }
        removeServer(server.key)
        return findFirstAvailableWithUnhealthyRemoval(hash)
    }

    override fun addVirtualNodes(vararg virtualNodes: S, sourceNode: S) {
        TODO("Not yet implemented")
    }

    override fun removeServer(key: String): S? {
        val hash = hashFunction.hash(key)
        return consistentHashRing - hash
    }
}
