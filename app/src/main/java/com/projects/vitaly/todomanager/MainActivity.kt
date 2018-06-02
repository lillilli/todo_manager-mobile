package com.projects.vitaly.todomanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.jetbrains.anko.toast

import java.net.URI;
import java.net.URISyntaxException
import org.java_websocket.handshake.ServerHandshake
import java.time.format.DateTimeFormatter

data class WSMessage(
        val type: String,
        val data: Any
)

data class AuthMessage (
        val id: Int,
        val token: String
)

data class Task (
        val id: Int,
        val user_id: Int,
        val order: Int,
        val text: String,
        val priority: String,
        val created_at: String,
        val finished_at: String
)


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val g = Gson()

        val wsClient = object : WebSocketClient(URI("ws://heilys.ru:8081")) {
            override fun onOpen(handshakedata: ServerHandshake) {
                Log.e("on open", handshakedata.toString())

                val authMessage = WSMessage("auth", AuthMessage(2, "680727359b47992f"))
                send(g.toJson(authMessage))

                val getTaskMessage = WSMessage("task_get", "")
                send(g.toJson(getTaskMessage))
            }

            override fun onMessage(message: String) {
                val msg = g.fromJson(message, WSMessage::class.java)
                Log.e("on message", message)
                Log.e("message type", msg.type)
                Log.e("message data", msg.data.toString())

                when (msg.type) {
                    "auth" -> Log.e("on auth", msg.data.toString())
                    "task_add" -> {
                        val task = g.fromJson(g.toJson(msg.data), Task::class.java)
                        Log.e("on add", task.toString())
                    }
                    "task_get" -> {
                        Log.e("", "-------------------------------------")
                        val tasks = g.fromJson(g.toJson(msg.data), Array<Task>::class.java)
                        for (task in tasks)
                            Log.e("task", task.toString())
                        Log.e("", "-------------------------------------")
                    }
                    else -> { // Note the block
                        print("x is neither 1 nor 2")
                    }
                }
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                Log.e("on close", "code = $code, reason = $reason, remote = $remote")
            }

            override fun onError(ex: Exception) {
                Log.e("on error", "message: ${ex.message}")
            }
        }

        wsClient.connect()
        wsClient.setAttachment("{\"type\": \"auth\", \"data\": {\"id\": \"2\", \"token\": \"680727359b47992f\"}")

        button.setOnClickListener {
            toast("Kotlin Android")
        }
    }

    private fun updateTasks(tasks: Array<Task>) {
        for (i in tasks)
            Log.e("task", i.toString())
    }
}
