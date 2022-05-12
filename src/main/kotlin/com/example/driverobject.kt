package com.example

import com.example.plugins.Generator
import com.example.plugins.driversConnections
import io.ktor.util.Identity.decode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

object DriverGenerator{
    var job: Job? = null
    fun connect() {
for(connection in driversConnections){
        job = GlobalScope.launch {
            delay(5000)
            while (true){
                connection.channel.consumeEach { value ->
                    buschannel.emit(BusConnection(connection.id.toInt(), value.toString()))
                }


            }

        }

    }}
    fun disconnect() {
        job?.cancel()
        job = null
    }
}

val buschannel = MutableSharedFlow<BusConnection>(0,1, onBufferOverflow = BufferOverflow.DROP_LATEST)