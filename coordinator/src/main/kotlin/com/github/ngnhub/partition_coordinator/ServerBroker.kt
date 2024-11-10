package com.github.ngnhub.partition_coordinator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class ServerBroker<S : Server>(
    private val scope: CoroutineScope,
    private val newServersChannel: Channel<S>,
    private val downServiceChannel: Channel<S>
) {

    fun sendNewServer(server: Server) = scope.launch {
        newServersChannel.send(newServersChannel.receive())
    }

    fun subscribeOnNewServers(): Channel<S> = newServersChannel

    fun sendDownServer(server: S) = scope.launch {
        newServersChannel.send(downServiceChannel.receive())
    }

    fun subscribeOnDownServer(): ReceiveChannel<S> = downServiceChannel

    fun close() {
        newServersChannel.close()
        downServiceChannel.close()
        scope.cancel()
    }
}
