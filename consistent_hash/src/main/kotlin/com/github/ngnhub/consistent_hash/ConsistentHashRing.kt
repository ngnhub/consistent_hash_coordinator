package com.github.ngnhub.consistent_hash

import com.github.ngnhub.consistent_hash.exception.CollisionException
import java.math.BigInteger
import java.util.concurrent.ConcurrentSkipListMap

class ConsistentHashRing<V> {

    private val map: ConcurrentSkipListMap<BigInteger, V?> = ConcurrentSkipListMap()

    val size get() = map.size

    /**
     * The method does not resolve collisions.
     * The frequency of collisions depends on the chosen HashFunction hashing algorithm.
     * It is recommended to choose an optimal algorithm with the lowest possible collision
     * probability without sacrificing performance (for example, MurmurHashing)
     *
     * @throws CollisionException when collision has occurred
     */
    operator fun set(key: BigInteger, value: V) {
        if (map.containsKey(key)) {
            throw CollisionException("Collision has occurred for key $key. The key must be modified")
        }
        map[key] = value
    }

    /**
     * Retrieve the closest value to the provided key; an exact key match is not required
     */
    operator fun get(key: BigInteger): V? {
        if (map.isEmpty()) {
            return null
        }
        if (map.containsKey(key)) {
            return map[key]
        }
        val ceilingEntry = map.ceilingEntry(key) ?: map.firstEntry()
        return ceilingEntry.value
    }

    fun nextAfter(key: BigInteger): V? {
        if (map.isEmpty()) {
            return null
        }
        val higherEntry = map.higherEntry(key) ?: map.firstEntry()
        return if (higherEntry!!.key == key) null else higherEntry.value
    }

    operator fun minus(key: BigInteger): V? {
        return map.remove(key)
    }
}
