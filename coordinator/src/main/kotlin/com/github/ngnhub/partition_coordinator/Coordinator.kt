package com.github.ngnhub.partition_coordinator

interface Coordinator<K, S : Server> {

    val serversCount: Int

    operator fun plus(server: S)

    operator fun get(key: K): Any?

    fun addVirtualNodes(vararg virtualNodes: S, sourceNode: S)

    fun removeServer(key: K): S?
}
