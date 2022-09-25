package com.tenalis.mart.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso
import com.tenalis.mart.R
import com.tenalis.mart.models.SliderItem

class SliderAdapter :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {
    private var mSliderItems: MutableList<SliderItem> = ArrayList()
    fun renewItems(sliderItems: MutableList<SliderItem>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

//    fun deleteItem(position: Int) {
//        mSliderItems.removeAt(position)
//        notifyDataSetChanged()
//    }
//
//    fun addItem(sliderItem: SliderItem) {
//        mSliderItems.add(sliderItem)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: SliderItem = mSliderItems[position]
        viewHolder.textViewDescription.text = sliderItem.description
        viewHolder.textViewDescription.textSize = 16f
        viewHolder.textViewDescription.setTextColor(Color.WHITE)
        Picasso.get().load(sliderItem.imageUrl).placeholder(R.drawable.logo)
            .into(viewHolder.imageViewBackground)
//        viewHolder.itemView.setOnClickListener {
//            Toast.makeText(context, "This is item in position $position", Toast.LENGTH_SHORT)
//                .show()
//        }
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
        var textViewDescription: TextView = itemView.findViewById(R.id.tv_auto_image_slider)
    }

}
