package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureSockets

import io.ktor.serialization.*

import io.ktor.server.engine.*
import io.ktor.server.netty.*



fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        configureSockets()
        configureSerialization()
    }.start(wait = true)
}
