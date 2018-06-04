package com.projects.vitaly.todomanager

import android.os.Bundle
import android.app.Activity
import android.support.v7.widget.Toolbar
import android.view.View

class Task (
        val id: Int? = 0,
        val user_id: Int? = 0,
        val order: Int? = 0,
        val text: String? = "",
        val priority: String? = "",
        val created_at: String? = "",
        val finished_at: String? = ""
)

class TaskActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task)

        val mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        mToolbar.title = getString(R.string.app_name)
        mToolbar.setNavigationIcon(R.drawable.ic_back)
        mToolbar.setNavigationOnClickListener { finish() }
    }
}