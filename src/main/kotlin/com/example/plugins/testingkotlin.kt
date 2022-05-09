package com.example.plugins



import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
data class user(var id : String, var frame : String)
object Generator {

    var job: Job? = null
    fun connect() {
        job = GlobalScope.launch {
            for (i in 0 until 100) {
                sf.send(user("1", (1..10).random().toString()))
                delay(1000)
            }
        }
    }
    fun disconnect() {
        job?.cancel()
        job = null
    }
}
//val sf = MutableSharedFlow<Int>()
val sf = BroadcastChannel<user>(1)

fun main(): Unit {
    val action : (i : user) -> Unit = {
println(user(it.id, it.frame))
    }

        GlobalScope.launch {
            sf.consumeEach{ response ->
                action(response)
            }
        }



    while (true) {
        if (readLine() == "1") {
            Generator.connect()
        } else {
            Generator.disconnect()
        }
    }
    //дис
}