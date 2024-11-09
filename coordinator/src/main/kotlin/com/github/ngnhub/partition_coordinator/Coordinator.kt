package com.github.ngnhub.partition_coordinator

interface Coordinator<K> {

    val serversCount: Int

    fun addServer(server: Server<K>)

    fun addVirtualNodes(vararg virtualNodes: K, sourceNode: K)

    fun removeServer(key: K): Server<K>?

    operator fun set(key: K, value: Any)

    operator fun get(key: K): Any?
}
