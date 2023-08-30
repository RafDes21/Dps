package models

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafdev.apptv.R


class DataAdapter(private val dataItems: List<DataItem>) : RecyclerView.Adapter<DataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
       return DataViewHolder(layoutInflater.inflate(R.layout.item_card, parent, false))

    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item:DataItem = dataItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataItems.size
}

