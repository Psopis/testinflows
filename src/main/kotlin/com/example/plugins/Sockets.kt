package com.example.plugins

import com.example.BusConnection
import com.example.DriverConnections
import com.example.DriverGenerator
import com.example.buschannel
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.util.*

var driversConnections = Collections.synchronizedList<DriverConnections>(mutableListOf())


@OptIn(ObsoleteCoroutinesApi::class)
fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }




    routing {
        webSocket("/all") {
var x = 0
            GlobalScope.launch(Dispatchers.Default) {

                buschannel.collect(){ response ->
                    x += 1

                    println(x)

                    send(response.toString())
                    delay(5000)
                }

            }

                while (true) {
                    delay(2000)
                    if (driversConnections != null) {
                        DriverGenerator.connect()
                    } else {
                        DriverGenerator.disconnect()
                    }
                }



        }




//                    driversConnections.asFlow()
//                        .collect { response ->
//
//                            response.channel.consumeEach { value ->
//                                send(BusConnection(response.id.toInt(), value.toString()).toString())
//                            }
//                        }
//                }





        webSocket("/driver/{idDriver}") {
            val idDriver = call.parameters["idDriver"]
            if (idDriver == null) close(CloseReason(CloseReason.Codes.NORMAL, "Driver is null"))
            else {
                var driver = driversConnections.find { idDriver == it.id }
                if (driver != null) {
                    driver.connection = this
                } else {
                    driver = DriverConnections(idDriver, this)
                    driver.connection = this
                    driversConnections.add(driver)
                }
                driver.startBroadcast()
            }
        }

    }
}
