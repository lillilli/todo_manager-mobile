package com.projects.vitaly.todomanager

import android.support.v7.widget.RecyclerView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.task_list.view.*

class RecycleViewTaskAdapter(val items : ArrayList<Task>, val context: Context) : RecyclerView.Adapter<ViewHolder>()
{
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.rvTaskname?.text = items.get(position).text
        holder?.rvTaskprior?.text = items.get(position).priority
        holder?.rvTaskcreatedat?.text = items.get(position).created_at
        holder?.rvTaskduedate?.text = items.get(position).finished_at
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.task_list, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val rvTaskname = view.rv_tasklist_item_nametask
    val rvTaskprior = view.rv_tasklist_item_priority
    val rvTaskcreatedat = view.rv_tasklist_item_createdate
    val rvTaskduedate = view.rv_tasklist_item_duedate
}