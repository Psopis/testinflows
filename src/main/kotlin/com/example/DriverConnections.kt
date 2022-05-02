package com.example

import io.ktor.websocket.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn

class DriverConnections(
    var id:String = "",
    var connection: DefaultWebSocketSession? = null,
//   val channel : SharedFlow<Frame> = MutableSharedFlow(0,1)
  val channel : BroadcastChannel<Frame> = BroadcastChannel<Frame>(1)//Channel<Frame>().broadcast()
){
    suspend fun startBroadcast(){

        connection?.let { connect ->
            for (frame in connect.incoming) {
                println(this)

                channel.send(frame.copy())
                }
            }

        }
    }
