package com.niveon.mealrecipe.api

import com.niveon.mealrecipe.model.AreaList
import com.niveon.mealrecipe.model.CategoryList
import com.niveon.mealrecipe.model.FoodList
import com.niveon.mealrecipe.util.Constants.CATEGORIES
import com.niveon.mealrecipe.util.Constants.FILTER
import com.niveon.mealrecipe.util.Constants.LIST
import com.niveon.mealrecipe.util.Constants.LOOKUP
import com.niveon.mealrecipe.util.Constants.RANDOM
import com.niveon.mealrecipe.util.Constants.SEARCH
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(RANDOM)
    suspend fun getFoodRandom() : Response<FoodList>
    @GET(CATEGORIES)
    suspend fun getCategoriesList() : Response<CategoryList>
    @GET(SEARCH)
    suspend fun getFoodList(@Query("f") latter : String) : Response<FoodList>

    @GET(SEARCH)
    suspend fun searchList(@Query("s") latter : String) : Response<FoodList>

    @GET(FILTER)
    suspend fun filterList(@Query("c") latter : String) : Response<FoodList>

    @GET(FILTER)
    suspend fun filterList2(@Query("a") latter : String) : Response<FoodList>

    @GET(LIST)
    suspend fun getAreasList() : Response<AreaList>

    @GET(LOOKUP)
    suspend fun getFoodDetails(@Query("i") id : Int) : Response<FoodList>
}