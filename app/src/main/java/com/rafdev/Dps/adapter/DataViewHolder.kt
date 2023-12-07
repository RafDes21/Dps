package com.rafdev.Dps.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rafdev.Dps.models.VideoItemModel
import com.rafdev.dps.databinding.ItemCardBinding


class DataViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemCardBinding.bind(view)

    fun bind(dataItem: VideoItemModel) {
        binding.tvTitle.text = dataItem.name_live
        binding.tvDescription.text = dataItem.timezone
        val backgroundColor = Color.parseColor(dataItem.color)
        itemView.setBackgroundColor(backgroundColor)
    }
}