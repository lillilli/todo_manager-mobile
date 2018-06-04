package com.projects.vitaly.todomanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Canvas
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


import java.net.URI
import kotlinx.android.synthetic.main.add_task_dialog.view.*
import org.jetbrains.anko.toast
import android.support.v7.widget.RecyclerView




class MainActivity : AppCompatActivity() {

    private var listNotes = ArrayList<Task>()
    private var taskManager: TaskManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        var _self = this

        var swipeController = SwipeController(object : SwipeControllerActions() {
            override fun onRightClicked(position: Int) {
               toast("Right Clicked!")
            }

            override fun onLeftClicked(position: Int) {
                toast("Left Clicked!")
            }
        })
        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(rv_tasklist)

        rv_tasklist.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })

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
