package com.github.ngnhub.partition_coordinator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class ServerBroker(
    private val scope: CoroutineScope,
    private val newServersChannel: Channel<Server>,
    private val downServiceChannel: Channel<Server>
) {

    fun sendNewServer(server: Server) = scope.launch {
        newServersChannel.send(newServersChannel.receive())
    }

    fun subscribeOnNewServers(): Channel<Server> = newServersChannel

    fun sendDownServer(server: Server) = scope.launch {
        newServersChannel.send(downServiceChannel.receive())
    }

    fun subscribeOnDownServer(): ReceiveChannel<Server> = downServiceChannel

    fun close() {
        newServersChannel.close()
        downServiceChannel.close()
        scope.cancel()
    }
}
