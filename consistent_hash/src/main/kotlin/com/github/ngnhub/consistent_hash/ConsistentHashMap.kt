package com.github.ngnhub.consistent_hash

import com.github.ngnhub.consistent_hash.exception.CollisionException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.math.BigInteger
import java.util.concurrent.ConcurrentSkipListMap

private val logger = KotlinLogging.logger {}

class ConsistentHashMap<K, V>(private val hashFunction: HashFunction<K>) {

    private val treeMap: ConcurrentSkipListMap<BigInteger, V?> = ConcurrentSkipListMap()

    /**
     * The method does not resolve collisions.
     * The frequency of collisions depends on the chosen HashFunction hashing algorithm.
     * It is recommended to choose an optimal algorithm with the lowest possible collision
     * probability without sacrificing performance (for example, MurmurHashing)
     *
     * @throws CollisionException when collision has occurred
     */
    operator fun set(key: K, value: V) {
        val index = hashFunction.hash(key)
        if (treeMap.containsKey(index)) {
            throw CollisionException("Collision has occurred for key $key. The key must be modified")
        }
        logger.debug { "Put '$value' at index '$index'" }
        treeMap[index] = value
    }

    /**
     * Retrieve the closest value to the provided key; an exact key match is not required
     */
    operator fun get(key: K): V? {
        if (treeMap.isEmpty()) {
            return null
        }
        val keyHash = hashFunction.hash(key)
        if (treeMap.containsKey(keyHash)) {
            return treeMap[keyHash]
        }
        val ceilingEntry = treeMap.ceilingEntry(keyHash)
        return if (ceilingEntry != null) ceilingEntry.value else {
            treeMap.ceilingEntry(BigInteger.ZERO).value
        }
    }

    fun nextAfter(key: K): V? {
        if (treeMap.isEmpty()) {
            return null
        }
        val keyHash = hashFunction.hash(key)
        val higherEntry = treeMap.higherEntry(keyHash)
        return if (higherEntry != null) higherEntry.value else {
            treeMap.ceilingEntry(BigInteger.ZERO).value
        }
    }
}
