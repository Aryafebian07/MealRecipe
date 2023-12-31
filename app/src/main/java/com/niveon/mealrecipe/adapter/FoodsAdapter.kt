package com.niveon.mealrecipe.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.niveon.mealrecipe.databinding.ItemFoodsBinding
import com.niveon.mealrecipe.model.FoodList
import javax.inject.Inject

class FoodsAdapter @Inject constructor() : RecyclerView.Adapter<FoodsAdapter.ViewHolder>() {

    private lateinit var binding: ItemFoodsBinding
    private var moviesList = emptyList<FoodList.Meal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemFoodsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moviesList[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount() = moviesList.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: FoodList.Meal) {
            binding.apply {
                itemFoodsImg.load(item.strMealThumb) {
                    crossfade(true)
                    crossfade(500)
                }
                itemFoodsTitle.text = item.strMeal
                if (item.strCategory.isNullOrEmpty().not()) {
                    itemFoodsCategory.text = item.strCategory
                    itemFoodsCategory.visibility = View.VISIBLE
                } else {
                    itemFoodsCategory.visibility = View.GONE
                }
                if (item.strArea.isNullOrEmpty().not()) {
                    itemFoodsArea.text = item.strArea
                    itemFoodsArea.visibility = View.VISIBLE
                } else {
                    itemFoodsArea.visibility = View.GONE
                }
                if (item.strSource != null) {
                    itemFoodsCount.visibility = View.VISIBLE
                } else {
                    itemFoodsCount.visibility = View.GONE
                }

                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(item)
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((FoodList.Meal) -> Unit)? = null

    fun setOnItemClickListener(listener: (FoodList.Meal) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<FoodList.Meal>) {
        val moviesDiffUtil = MoviesDiffUtils(moviesList, data)
        val diffUtils = DiffUtil.calculateDiff(moviesDiffUtil)
        moviesList = data
        diffUtils.dispatchUpdatesTo(this)
    }

    class MoviesDiffUtils(private val oldItem: List<FoodList.Meal>, private val newItem: List<FoodList.Meal>) : DiffUtil.Callback() {
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
}