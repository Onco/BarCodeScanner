package com.atlasstudio.barcodescanner.ui.codeslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atlasstudio.barcodescanner.data.Code
import com.atlasstudio.barcodescanner.databinding.ItemScannerBinding

class CodesListAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Code, CodesListAdapter.CodesListViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodesListViewHolder {
        val binding = ItemScannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CodesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CodesListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CodesListViewHolder(private val binding: ItemScannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                textViewCode.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        //val code = getItem(position)
                        listener.onItemClick(position)
                    }
                }

                buttonDelete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        //val code = getItem(position)
                        listener.onButtonDeleteClick(position)
                    }
                }
            }
        }

        fun bind(code: Code) {
            binding.apply {
                code.value?.let {
                    textViewCode.text = it
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onButtonDeleteClick(position: Int)
    }

    class DiffCallback : DiffUtil.ItemCallback<Code>() {
        override fun areItemsTheSame(oldItem: Code, newItem: Code) =
            oldItem.value == newItem.value

        override fun areContentsTheSame(oldItem: Code, newItem: Code) =
            oldItem == newItem
    }
}