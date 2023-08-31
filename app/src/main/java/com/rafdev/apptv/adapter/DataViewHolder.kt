package com.rafdev.apptv.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rafdev.apptv.databinding.ItemCardBinding
import com.rafdev.apptv.models.VideoItemModel


class DataViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemCardBinding.bind(view)

    fun bind(dataItem: VideoItemModel) {
        binding.tvTitle.text = dataItem.name_live
        binding.tvDescription.text = dataItem.timezone
        val backgroundColor = Color.parseColor(dataItem.color)
        itemView.setBackgroundColor(backgroundColor)
    }
}