package com.projects.vitaly.todomanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

import java.net.URI
import kotlinx.android.synthetic.main.add_task_dialog.view.*


class MainActivity : AppCompatActivity() {

    private var listNotes = ArrayList<Task>()
    private var taskManager: TaskManager? = null

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

        this.taskManager = object : TaskManager(URI("ws://heilys.ru:8081"), listNotes) {
            override fun onTasksUpdate() {
                h.sendEmptyMessage(0)
            }
        }
    }

    fun openAddTaskDialog(view: View) {
        val tm = this.taskManager

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.add_task_dialog, null)

        builder.setView(view)
        builder.setNegativeButton("cancel", DialogInterface.OnClickListener{dialog, which ->  dialog.dismiss()})
        builder.setPositiveButton("create") { dialog, which ->
            val data = AddMessage(view.et_text.text.toString(), view.et_priority.selectedItem.toString())
            tm?.addTask(data)
        }

        val dialog = builder.create()

        dialog.setTitle("Add new task")

        dialog.show()
    }
}
