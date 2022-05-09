package com.example

import com.example.plugins.driversConnections
import com.example.plugins.sf
import com.example.plugins.user
import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch





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
