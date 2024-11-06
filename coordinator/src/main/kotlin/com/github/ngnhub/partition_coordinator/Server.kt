package com.github.ngnhub.partition_coordinator

import com.github.ngnhub.consistent_hash.model.Hashed
import java.math.BigInteger

interface Server<K> : Hashed {

    override val hash: BigInteger

    val key: String

    val virtualNodesKeys: Set<K>

    fun health(): Boolean
}
