package com.github.ngnhub.partition_coordinator

interface StorageProvider<K> {

    fun read(key: K, server: Server<K>): ByteArray?

    fun insert(key: K, server: Server<K>): ByteArray?

    fun update(vararg params: Any, server: Server<K>): ByteArray?

    fun delete(vararg params: Any, server: Server<K>): ByteArray?

    fun reDistribute(from: Server<K>, to: Server<K>)
}
