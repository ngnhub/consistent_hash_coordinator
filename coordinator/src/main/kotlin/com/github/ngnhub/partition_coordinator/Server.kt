package com.github.ngnhub.partition_coordinator

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

private const val CONNECTION_TIME_OUT = 3000
private val logger = KotlinLogging.logger {}

interface Server {

    val host: String

    val port: Int

    val key: String // todo shouldn't be here. it is a ConsistentHash's responsibility
        get() = host + port

    val virtualNodesKeys: Set<String>

    fun health(): Boolean {
        Socket().use { socket ->
            try {
                socket.connect(InetSocketAddress(host, port), CONNECTION_TIME_OUT);
                return true
            } catch (ex: IOException) {
                logger.error(ex) { "Error while connecting to $host:$port" }
                return false
            }
        }
    }

    fun read(key: String): Any? // todo : extra responsibility

    fun insert(key: String, value: Any?) // todo : extra responsibility

    fun reDistribute(from: Server) //todo to another class
}
