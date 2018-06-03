package com.projects.vitaly.todomanager

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

import java.net.URI


class MainActivity : AppCompatActivity() {

    private var listNotes = ArrayList<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var _self = this

        val h = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: android.os.Message) {
                rv_tasklist.layoutManager = LinearLayoutManager(_self)
                rv_tasklist.adapter = RecycleViewTaskAdapter(listNotes,_self)
            }
        }

        val wsClient = object : TaskManager(URI("ws://heilys.ru:8081"), listNotes) {
            override fun onTasksUpdate() {
                h.sendEmptyMessage(0)
            }
        }
    }
}
