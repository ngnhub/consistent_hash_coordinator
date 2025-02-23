package com.github.ngnhub.partition_coordinator

import com.github.ngnhub.consistent_hash.HashFunction
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.IOException
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.Socket

private const val CONNECTION_TIME_OUT = 3000
private val logger = KotlinLogging.logger {}

abstract class Server(
    val host: String,
    val port: Int,
    val key: String = host + port,
    val virtualNodesKeys: Set<String> = mutableSetOf()
) {

    open lateinit var hash: BigInteger

    open fun health(): Boolean = defaultHealthCheck() // todo: 'is' convention

    private fun defaultHealthCheck(): Boolean {
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

    abstract fun reDistribute(from: Server, by: HashFunction<String>)

    abstract fun moveEverything(to: Server)
    //todo how the type can be restricted by the generic??
}
