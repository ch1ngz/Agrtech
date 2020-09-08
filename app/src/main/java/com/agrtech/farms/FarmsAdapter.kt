package com.agrtech.farms

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agrtech.R

class FarmsAdapter(
    private val onFarmClicked: (Farm) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = listOf<Farm>()

    fun setItems(new: List<Farm>) {
        data = new
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(parent, onFarmClicked)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).onBind(data[position])
    }

    private class ViewHolder(
        parent: ViewGroup,
        private val onFarmClicked: (Farm) -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_farm, parent, false)
    ) {
        private val textView = itemView as TextView

        fun onBind(item: Farm) {
            textView.text = item.name
            textView.setOnClickListener { onFarmClicked(item) }
        }
    }
}