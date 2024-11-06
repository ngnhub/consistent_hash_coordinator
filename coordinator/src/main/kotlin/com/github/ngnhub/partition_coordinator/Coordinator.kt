package com.github.ngnhub.partition_coordinator

interface Coordinator<K> {

    fun addServer(server: Server<K>)

    fun addVirtualNodes(vararg virtualNodes: K, sourceNode: K)

    operator fun set(key: K, value: Any)

    operator fun get(key: K): Any?
}
