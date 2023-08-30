package models

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rafdev.apptv.databinding.ItemCardBinding


class DataViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemCardBinding.bind(view)

    fun bind(dataItem: DataItem) {
        binding.textViewTitle.text = dataItem.title
        // Establece otros datos en las vistas según sea necesario
    }
}