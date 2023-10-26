package com.niveon.mealrecipe.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.niveon.mealrecipe.R
import com.niveon.mealrecipe.databinding.ItemCategoriesBinding
import com.niveon.mealrecipe.model.CategoryList
import javax.inject.Inject

class CategoriesAdapter @Inject constructor() : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private lateinit var binding: ItemCategoriesBinding
    var moviesList = emptyList<CategoryList.Category>()
    var selectedItem = -1
    private var areasAdapter: AreasAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moviesList[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount() = moviesList.size

    fun setAreasAdapter(adapter: AreasAdapter) {
        areasAdapter = adapter
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: CategoryList.Category) {
            binding.apply {
                itemCategoriesImg.load(item.strCategoryThumb) {
                    crossfade(true)
                    crossfade(500)
                }
                itemCategoriesTxt.text = item.strCategory

                if (selectedItem == adapterPosition) {
                    root.setBackgroundResource(R.drawable.bg_rounded_selcted)
                } else {
                    root.setBackgroundResource(R.drawable.bg_rounded)
                }

                root.setOnClickListener {
                    if (selectedItem != adapterPosition) {
                        selectedItem = adapterPosition
                        areasAdapter?.resetSelection()
                        notifyDataSetChanged()
                        onItemClickListener?.let {
                            it(item)
                        }
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((CategoryList.Category) -> Unit)? = null

    fun setOnItemClickListener(listener: (CategoryList.Category) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<CategoryList.Category>) {
        val moviesDiffUtil = MoviesDiffUtils(moviesList, data)
        val diffUtils = DiffUtil.calculateDiff(moviesDiffUtil)
        moviesList = data
        diffUtils.dispatchUpdatesTo(this)
    }

    class MoviesDiffUtils(private val oldItem: List<CategoryList.Category>, private val newItem: List<CategoryList.Category>) : DiffUtil.Callback() {
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