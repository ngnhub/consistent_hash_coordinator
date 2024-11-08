package com.github.ngnhub.partition_coordinator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class ServerBroker<K>(
    private val scope: CoroutineScope,
    private val newServersChannel: Channel<Server<K>>,
    private val downServiceChannel: Channel<Server<K>>
) {

    fun sendNewServer(server: Server<K>) = scope.launch {
        newServersChannel.send(newServersChannel.receive())
    }

    fun subscribeOnNewServers(): ReceiveChannel<Server<K>> = newServersChannel

    fun sendDownServer(server: Server<K>) = scope.launch {
        newServersChannel.send(downServiceChannel.receive())
    }

    fun subscribeOnDownServer(): ReceiveChannel<Server<K>> = downServiceChannel

    fun close() {
        newServersChannel.close()
        downServiceChannel.close()
        scope.cancel()
    }
}
