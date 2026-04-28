package com.cardiary.app.ui.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cardiary.app.R
import com.cardiary.app.data.model.HistoryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<HistoryItem, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvType: TextView = itemView.findViewById(R.id.tvType)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvCost: TextView = itemView.findViewById(R.id.tvCost)
        private val tvOdometer: TextView = itemView.findViewById(R.id.tvOdometer)
        private val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)

        @SuppressLint("SetTextI18n")
        fun bind(item: HistoryItem) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvDate.text = sdf.format(Date(item.getDate()))
            tvCost.text = "%.2f EUR".format(item.getCost())
            tvOdometer.text = "${item.getOdometer()} km"

            when (item) {
                is HistoryItem.Fuel -> {
                    tvType.text = "⛽ Fuel"
                    tvDetails.text = "${item.entry.liters}L @ ${item.entry.pricePerLiter} EUR/L"
                }
                is HistoryItem.Repair -> {
                    tvType.text = "🔧 Repair"
                    tvDetails.text = "${item.entry.category.name.replace("_", " ")} - ${item.entry.description}"
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return when {
                oldItem is HistoryItem.Fuel && newItem is HistoryItem.Fuel ->
                    oldItem.entry.id == newItem.entry.id
                oldItem is HistoryItem.Repair && newItem is HistoryItem.Repair ->
                    oldItem.entry.id == newItem.entry.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}