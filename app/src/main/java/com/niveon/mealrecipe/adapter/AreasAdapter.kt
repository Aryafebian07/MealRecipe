package com.niveon.mealrecipe.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.niveon.mealrecipe.R
import com.niveon.mealrecipe.databinding.ItemAreasBinding
import com.niveon.mealrecipe.databinding.ItemCategoriesBinding
import com.niveon.mealrecipe.model.AreaList
import com.niveon.mealrecipe.model.CategoryList
import javax.inject.Inject

class AreasAdapter @Inject constructor() : RecyclerView.Adapter<AreasAdapter.ViewHolder>() {
    private lateinit var binding: ItemAreasBinding
    var moviesList = emptyList<AreaList.Area>()
    var selectedItem = -1
    private var categoriesAdapter: CategoriesAdapter? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemAreasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moviesList[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount() = moviesList.size

    fun setCategoriesAdapter(adapter: CategoriesAdapter) {
        categoriesAdapter = adapter
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: AreaList.Area) {
            binding.apply {

                itemAreaTxt.text = item.strArea

                if (selectedItem == adapterPosition) {
                    root.setBackgroundResource(R.drawable.bg_rounded_selcted)
                } else {
                    root.setBackgroundResource(R.drawable.bg_rounded)
                }

                root.setOnClickListener {
                    if (selectedItem != adapterPosition) {
                        selectedItem = adapterPosition
                        notifyDataSetChanged()
                        categoriesAdapter?.resetSelection()
                        onItemClickListener?.let {
                            it(item)
                        }
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((AreaList.Area) -> Unit)? = null

    fun setOnItemClickListener(listener: (AreaList.Area) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<AreaList.Area>) {
        val moviesDiffUtil = MoviesDiffUtils(moviesList, data)
        val diffUtils = DiffUtil.calculateDiff(moviesDiffUtil)
        moviesList = data
        diffUtils.dispatchUpdatesTo(this)
    }

    class MoviesDiffUtils(private val oldItem: List<AreaList.Area>, private val newItem: List<AreaList.Area>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldItem.size
        }

        override fun getNewListSize(): Int {
            return newItem.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] === newItem[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItem[oldItemPosition] === newItem[newItemPosition]
        }
    }
    fun resetSelection() {
        selectedItem = -1
        notifyDataSetChanged()
    }
}