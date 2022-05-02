package com.example.plugins

import com.example.BusConnection
import com.example.DriverConnections
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
    suspend fun positionRecive(position: BroadcastChannel<Frame>): String {
// Не могу получить frame из канала
//  а так все работает, то есть ечли подключить 3 водителя до диспетчер будет видеть из id
        return ""
    }

    suspend fun performRequest(busId: Int, busPosition: BroadcastChannel<Frame>): BusConnection {
        delay(5000)
        return BusConnection(busId, positionRecive(busPosition))
    }

    routing {
        webSocket("/all") {
            GlobalScope.launch(Dispatchers.Default) {
                while (true) {
                    try {
                        driversConnections.asFlow().map { v ->
                            performRequest(v.id.toInt(), v.channel)
                        }.collect { response ->
                            send(response.toString())
                            println(response)
                        }
                    } catch (e: Throwable) {
                        println("Error $e")
                    }
                }
            }.join()
        }

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
