package com.example.plugins

import com.example.BusConnection
import com.example.DriverConnections
import com.example.geopos
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.util.*

var driversConnections = Collections.synchronizedList<DriverConnections>(mutableListOf())

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    suspend fun performRequest(request: Int, positionFrame: BroadcastChannel<Frame>):Int{
        delay(5000)
var a = request
        return a
    }
    routing {
        webSocket("/all") {
            GlobalScope.launch(Dispatchers.Default) {
                while (true) {
                    try {
                        driversConnections.asFlow().map { v ->
                            performRequest(v.id.toInt(), v.channel)
                        }.collect {

                                response ->
                            send(response.toString())
                            println(response)
                        }
                    } catch (e: Throwable) {
                        println("Error $e")
                    }
                }

            }.join()
        }
        webSocket("/dispetcher") {
            GlobalScope.launch() {
                while (true) {
                    delay(1000)

                    driversConnections.asFlow()

                        .collect() {

                            it.channel.consumeEach { value ->

                                send(value.copy())
                                println(BusConnection(it.id.toInt(), value.copy().toString()))
                            }
                        }
                    if (driversConnections.isEmpty()) {

                        close(CloseReason(CloseReason.Codes.NORMAL, "DriverConnections is null"))
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
        webSocket("/passenger/{idpassenger}") {
            val idpassenger = call.parameters["idpassenger"].toString()
            if (idpassenger == null) close()

            launch {
                var driver = driversConnections.find { idpassenger == it.id } ?: DriverConnections()


                println("driver $driver")
                driver?.let {

                    it.channel.consumeEach { frame ->
                        send(frame.copy())
                        println("frame.copy()  ${frame.copy()}")
                    }
                }
            }.join()

        }
    }
}
