package com.niveon.mealrecipe.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niveon.mealrecipe.util.Constants.FOOD_TABLE

@Entity(tableName = FOOD_TABLE)
data class FoodEntity(
    @PrimaryKey
    var id: Int = 0,
    var title: String = "",
    var img: String = ""
)