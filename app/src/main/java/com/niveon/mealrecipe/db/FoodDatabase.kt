package com.niveon.mealrecipe.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FoodEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}