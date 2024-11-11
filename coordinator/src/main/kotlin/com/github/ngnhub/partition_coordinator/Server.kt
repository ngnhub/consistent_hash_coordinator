package com.github.ngnhub.partition_coordinator

import com.github.ngnhub.consistent_hash.HashFunction
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

private const val CONNECTION_TIME_OUT = 3000
private val logger = KotlinLogging.logger {}

interface Server {

    val host: String

    val port: Int

    val key: String
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

    fun reDistribute(from: Server, hashFunction: HashFunction<String>)
    //todo how the type can be restricted by the generic??
}
