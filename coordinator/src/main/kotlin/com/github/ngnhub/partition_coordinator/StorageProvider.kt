package com.github.ngnhub.partition_coordinator

interface StorageProvider<K> {

    fun read(key: K, server: Server<K>): Any?

    fun insert(key: K, server: Server<K>): Any?

    fun update(vararg params: Any, server: Server<K>): Any?

    fun delete(vararg params: Any, server: Server<K>): Any?

    fun reDistribute(from: Server<K>, to: Server<K>)
}
