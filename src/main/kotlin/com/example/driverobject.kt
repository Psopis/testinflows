package com.example

import com.example.plugins.driversConnections
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

object DriverGenerator{
    var job: Job? = null
    fun connect() {
for(connection in driversConnections){
        job = GlobalScope.launch {
            while (true){
                connection.channel.consumeEach { value ->
                    buschannel.send(BusConnection(connection.id.toInt(), value.toString()))
                }

            }
        }

    }}
    fun disconnect() {
        job?.cancel()
        job = null
    }
}
val buschannel= BroadcastChannel<BusConnection>(1)