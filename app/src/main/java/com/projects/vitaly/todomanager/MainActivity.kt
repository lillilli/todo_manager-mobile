package com.projects.vitaly.todomanager

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.jetbrains.anko.toast

import java.net.URI;
import java.net.URISyntaxException
import org.java_websocket.handshake.ServerHandshake
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private var listNotes = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var _self = this

        val h = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                var notesAdapter = NotesAdapter(_self, listNotes)
                lvNotes.adapter = notesAdapter
                lvNotes.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
                    Toast.makeText(_self, "Click on " + listNotes[position].text, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val wsClient = object : TaskManager(URI("ws://heilys.ru:8081"), listNotes) {
            override fun onTasksUpdate() {
                h.sendEmptyMessage(0)
            }
        }
    }

    inner class NotesAdapter : BaseAdapter {

        private var notesList = ArrayList<Task>()
        private var context: Context? = null

        constructor(context: Context, notesList: ArrayList<Task>) : super() {
            this.notesList = notesList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.note, parent, false)
                vh = ViewHolder(view)
                view.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.tvTitle.text = notesList[position].text
            vh.tvContent.text = notesList[position].text

            return view
        }

        override fun getItem(position: Int): Any {
            return notesList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return notesList.size
        }
    }

    private class ViewHolder(view: View?) {
        val tvTitle: TextView
        val tvContent: TextView

        init {
            this.tvTitle = view?.findViewById(R.id.tvTitle) as TextView
            this.tvContent = view?.findViewById(R.id.tvContent) as TextView
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val g = Gson()
//
//        val wsClient = object : WebSocketClient(URI("ws://heilys.ru:8081")) {
//            override fun onOpen(handshakedata: ServerHandshake) {
//                Log.e("on open", handshakedata.toString())
//
//                val authMessage = WSMessage("auth", AuthMessage(2, "680727359b47992f"))
//                send(g.toJson(authMessage))
//
//                val getTaskMessage = WSMessage("task_get", "")
//                send(g.toJson(getTaskMessage))
//            }
//
//            override fun onMessage(message: String) {
//                val msg = g.fromJson(message, WSMessage::class.java)
//                Log.e("on message", message)
//                Log.e("message type", msg.type)
//                Log.e("message data", msg.data.toString())
//
//                when (msg.type) {
//                    "auth" -> Log.e("on auth", msg.data.toString())
//                    "task_add" -> {
//                        val task = g.fromJson(g.toJson(msg.data), Task::class.java)
//                        Log.e("on add", task.toString())
//                    }
//                    "task_get" -> {
//                        Log.e("", "-------------------------------------")
//                        val tasks = g.fromJson(g.toJson(msg.data), Array<Task>::class.java)
//                        for (task in tasks)
//                            Log.e("task", task.toString())
//                        Log.e("", "-------------------------------------")
//                    }
//                    else -> { // Note the block
//                        print("x is neither 1 nor 2")
//                    }
//                }
//            }
//
//            override fun onClose(code: Int, reason: String, remote: Boolean) {
//                Log.e("on close", "code = $code, reason = $reason, remote = $remote")
//            }
//
//            override fun onError(ex: Exception) {
//                Log.e("on error", "message: ${ex.message}")
//            }
//        }
//
//        wsClient.connect()
//        wsClient.setAttachment("{\"type\": \"auth\", \"data\": {\"id\": \"2\", \"token\": \"680727359b47992f\"}")
//    }
}
