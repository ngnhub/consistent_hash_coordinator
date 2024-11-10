package com.github.ngnhub.partition_coordinator

interface Coordinator<K, S : Server> {

    val serversCount: Int

    fun addServer(server: S)

    fun addVirtualNodes(vararg virtualNodes: S, sourceNode: S)

    fun removeServer(key: K): S?

    operator fun set(key: K, value: Any)

    operator fun get(key: K): Any?
}
