package com.github.ngnhub.partition_coordinator.impl

import com.github.ngnhub.consistent_hash.ConsistentHashRing
import com.github.ngnhub.consistent_hash.HashFunction
import com.github.ngnhub.consistent_hash.impl.MurmurHashFunction
import com.github.ngnhub.partition_coordinator.Coordinator
import com.github.ngnhub.partition_coordinator.Server
import com.github.ngnhub.partition_coordinator.exception.NoAvailableSever
import java.math.BigInteger
import java.util.concurrent.locks.ReentrantLock

class DefaultCoordinator<S : Server>(
    private val hashFunction: HashFunction<String> = MurmurHashFunction(),
    private val consistentHashRing: ConsistentHashRing<S> = ConsistentHashRing(),
    private val lock: ReentrantLock = ReentrantLock()
) : Coordinator<String, S> {

    override val serversCount: Int
        get() = consistentHashRing.size

    override fun plus(server: S) { //todo: put instead plus?
        try {
            lock.lock()
            val newNodeHash = hashFunction.hash(server.key)
            server.hash = newNodeHash
            consistentHashRing[newNodeHash] = server
            nextAvailableServer(newNodeHash + BigInteger.ONE)?.let {
                if (server.key != it.key) {
                    server.reDistribute(it, hashFunction)
                }
            }
        } catch (e: Exception) {
            this - server.key
            throw e
        } finally {
            lock.unlock()
        }
    }

    override fun get(key: String): S {
        val hash = hashFunction.hash(key)
        return nextAvailableServer(hash) ?: throw NoAvailableSever()
    }

    private fun nextAvailableServer(hash: BigInteger): S? {
        val nextAfter = consistentHashRing[hash] ?: return null
        if (nextAfter.health()) {
            return nextAfter
        }
        this - nextAfter.key
        return nextAvailableServer(nextAfter.hash + BigInteger.ONE)
    }

    override fun addVirtualNodes(vararg virtualNodes: S, sourceNode: S) {
//        TODO("Not yet implemented")
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun minus(key: String): S? {
        val hash = hashFunction.hash(key)
        return consistentHashRing - hash
    }
}
