package com.projects.vitaly.todomanager

import android.util.Log
import com.google.gson.Gson
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

data class WSMessage(
        val type: String,
        val data: Any
)

data class AuthMessage (
        val id: Int,
        val token: String
)

data class DeleteMessage (
        val id: Int
)

open class TaskManager(private var url: URI, var tasks: ArrayList<Task>) {

    private var wsClient: WebSocketClient? = null

    private var g = Gson()

    private fun onOpen(handshakedata: ServerHandshake) {
        Log.e("on open", handshakedata.toString())

        val authMessage = WSMessage("auth", AuthMessage(2, "680727359b47992f"))
        wsClient?.send(g.toJson(authMessage))

        val getTaskMessage = WSMessage("task_get", "")
        wsClient?.send(g.toJson(getTaskMessage))
    }

    private fun onMessage(message: String) {
        val msg = g.fromJson(message, WSMessage::class.java)
        Log.e("on message", message)
        Log.e("message type", msg.type)
        Log.e("message data", msg.data.toString())

        when (msg?.type) {
            "auth" -> Log.e("on auth", msg.data.toString())
            "task_add" -> {
                val task = g.fromJson(g.toJson(msg.data), Task::class.java)
                Log.e("on add", task.toString())
                this.tasks.add(task)
                this.onTasksUpdate()
            }
            "task_get" -> {
                Log.e("@@@@@@@@@@@@@@@@@", this.tasks.toString())
                val tasks = g.fromJson(g.toJson(msg.data), Array<Task>::class.java)

                for (currentTask in this.tasks) {
                    if (this.indexOf(tasks, currentTask) == -1) {
                        this.tasks.remove(currentTask)
                    }
                }

                for (newTask in tasks) {
                    if (this.indexOf(this.tasks, newTask) == -1) {
                        this.tasks.add(newTask)
                    }
                }

                for (task in this.tasks)
                    Log.e("task", task.toString())

                this.onTasksUpdate()
                Log.e("", "-------------------------------------")
            }
            "task_update" -> {
                val task = g.fromJson(g.toJson(msg.data), Task::class.java)
                Log.e("on delete", task.toString())

                val index = indexOf(this.tasks, task.id!!)
                if (index != -1) {
                    this.tasks[index] = task
                    this.onTasksUpdate()
                }
            }
            "task_delete" -> {
                val data = g.fromJson(g.toJson(msg.data), DeleteMessage::class.java)
                Log.e("on delete", data.toString())

                val index = indexOf(this.tasks, data.id)
                if (index != -1) {
                    this.tasks.remove(this.tasks[index])
                    this.onTasksUpdate()
                }
            }
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
    }

    init {
        val _self = this

        this.wsClient = object : WebSocketClient(this.url) {
            override fun onOpen(handshakedata: ServerHandshake) {
                _self.onOpen(handshakedata)
            }

            override fun onMessage(message: String) {
                _self.onMessage(message)
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                Log.e("on close", "code = $code, reason = $reason, remote = $remote")
            }

            override fun onError(ex: Exception) {
                Log.e("on error", "message: ${ex.message}")
            }
        }

        (this.wsClient as WebSocketClient).connect()
    }

    private fun indexOf(tasks: ArrayList<Task>, taskID: Int) : Int {
        for ((i, v) in tasks.withIndex()) {
            if (v.id == taskID) {
                return i
            }
        }

        return -1
    }

    private fun indexOf(tasks: Array<Task>, task: Task) : Int {
        for ((i, v) in tasks.withIndex()) {
            if (v.id == task.id) {
                return i
            }
        }

        return -1
    }

    private fun indexOf(tasks: ArrayList<Task>, task: Task) : Int {
        for ((i, v) in tasks.withIndex()) {
            if (v.id == task.id) {
                return i
            }
        }

        return -1
    }

    private fun updateTasks(newTasks: Array<Task>) {
        for (currentTask in this.tasks) {
            if (this.indexOf(tasks, currentTask) == -1) {
                this.tasks.remove(currentTask)
            }
        }

        for (newTask in tasks) {
            if (this.indexOf(this.tasks, newTask) == -1) {
                this.tasks.add(newTask)
            }
        }
    }

    open fun onTasksUpdate() {
        Log.e("on task update", "function not implemented")
    }
}
