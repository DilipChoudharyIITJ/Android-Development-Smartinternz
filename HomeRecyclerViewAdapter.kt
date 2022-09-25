package com.tenalis.mart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tenalis.mart.R
import com.tenalis.mart.models.HomeItem

class HomeRecyclerViewAdapter(private val List: List<HomeItem>, val listener: OnItemClickListener) :
    RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val title: TextView = item.findViewById(R.id.title)
        val image: ImageView = item.findViewById(R.id.image)

        init {
            item.setOnClickListener {
                listener.onItemClick(title.text.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val element = List[position]
        holder.title.text = element.name
        holder.image.setImageResource(element.image)
    }

    interface OnItemClickListener {
        fun onItemClick(title: String)
    }

    override fun getItemCount(): Int {
        return List.size
    }
}
