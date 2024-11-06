package com.github.ngnhub.partition_coordinator

import java.math.BigInteger

interface Server<K> {

    val key: String

    val hash: BigInteger

    val virtualNodesKeys: Set<K>

    fun health(): Boolean
}