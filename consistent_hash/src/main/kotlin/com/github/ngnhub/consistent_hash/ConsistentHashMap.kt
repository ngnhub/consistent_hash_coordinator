package com.github.ngnhub.consistent_hash

import com.github.ngnhub.consistent_hash.exception.CollisionException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.math.BigInteger
import java.util.concurrent.ConcurrentSkipListMap

private val logger = KotlinLogging.logger {}

class ConsistentHashMap<K, V>(private val hashFunction: HashFunction<K>) {

    private val map: ConcurrentSkipListMap<BigInteger, V?> = ConcurrentSkipListMap() // todo: delegate?

    val size get() = map.size

    /**
     * The method does not resolve collisions.
     * The frequency of collisions depends on the chosen HashFunction hashing algorithm.
     * It is recommended to choose an optimal algorithm with the lowest possible collision
     * probability without sacrificing performance (for example, MurmurHashing)
     *
     * @throws CollisionException when collision has occurred
     */
    operator fun set(key: K, value: V) {
        val keyHash = hash(key)
        if (map.containsKey(keyHash)) {
            throw CollisionException("Collision has occurred for key $key. The key must be modified")
        }
        logger.debug { "Put '$value' at index '$keyHash'" }
        map[keyHash] = value
    }

    /**
     * Retrieve the closest value to the provided key; an exact key match is not required
     */
    operator fun get(key: K): V? {
        if (map.isEmpty()) {
            return null
        }
        val keyHash = hash(key)
        if (map.containsKey(keyHash)) {
            return map[keyHash]
        }
        val ceilingEntry = map.ceilingEntry(keyHash) ?: map.firstEntry()
        return ceilingEntry.value
    }

    fun nextAfter(key: K): V? {
        if (map.isEmpty()) {
            return null
        }
        val keyHash = hash(key)
        val higherEntry = map.higherEntry(keyHash) ?: map.firstEntry()
        return if (higherEntry!!.key == keyHash) null else higherEntry.value
    }

    operator fun minus(key: K): V? {
        val keyHash = hash(key)
        return map.remove(keyHash)
    }

    private fun hash(key: K): BigInteger {
        return hashFunction.hash(key)
    }
}
