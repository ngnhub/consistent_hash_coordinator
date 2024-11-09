package com.github.ngnhub.partition_coordinator

import com.github.ngnhub.consistent_hash.model.Hashed
import java.math.BigInteger

interface Server<K> : Hashed {

    override val hash: BigInteger

    val key: String // todo shouldn't be here. it is a ConsistentHash's responsibility

    val virtualNodesKeys: Set<K>

    fun health(): Boolean
}
