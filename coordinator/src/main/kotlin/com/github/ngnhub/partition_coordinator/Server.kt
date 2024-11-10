package com.github.ngnhub.partition_coordinator

interface Server<K> {

    val key: String // todo shouldn't be here. it is a ConsistentHash's responsibility

    val virtualNodesKeys: Set<K>

    fun health(): Boolean

    fun read(key: String): Any?

    fun insert(key: String, value: Any?)

    fun reDistribute(from: Server<K>)
}
