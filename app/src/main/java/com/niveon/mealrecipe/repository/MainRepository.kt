package com.niveon.mealrecipe.repository

import com.niveon.mealrecipe.api.ApiService
import com.niveon.mealrecipe.db.FoodDao
import com.niveon.mealrecipe.db.FoodEntity
import com.niveon.mealrecipe.model.AreaList
import com.niveon.mealrecipe.model.CategoryList
import com.niveon.mealrecipe.model.FoodList
import com.niveon.mealrecipe.util.DataStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiServices: ApiService, private val dao: FoodDao) {
    suspend fun getRandomFood(): Flow<Response<FoodList>> {
        return flow {
            emit(apiServices.getFoodRandom())
        }.flowOn(Dispatchers.IO)
    }
    suspend fun getCategoriesList(): Flow<DataStatus<CategoryList>> {
        return flow {
            emit(DataStatus.loading())
            //Response
            when (apiServices.getCategoriesList().code()) {
                in 200..202 -> {
                    emit(DataStatus.success(apiServices.getCategoriesList().body()))
                }
                422 -> {
                    emit(DataStatus.error(""))
                }
                in 400..499 -> {
                    emit(DataStatus.error(""))
                }
                in 500..599 -> {
                    emit(DataStatus.error(""))
                }
            }
        }.catch { emit(DataStatus.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getAreasList():Flow<DataStatus<AreaList>>{
        return flow {
            emit(DataStatus.loading())
            when (apiServices.getAreasList().code()) {
                in 200..202 -> {
                    emit(DataStatus.success(apiServices.getAreasList().body()))
                }
                422 -> {
                    emit(DataStatus.error(""))
                }
                in 400..499 -> {
                    emit(DataStatus.error(""))
                }
                in 500..599 -> {
                    emit(DataStatus.error(""))
                }
            }
        }
    }
    suspend fun getFoodsList(letter: String): Flow<DataStatus<FoodList>> {
        return flow {
            emit(DataStatus.loading())
            when (apiServices.getFoodList(letter).code()) {
                in 200..202 -> {
                    emit(DataStatus.success(apiServices.getFoodList(letter).body()))
                }
            }
        }.catch { emit(DataStatus.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getFoodsBySearch(letter: String): Flow<DataStatus<FoodList>> {
        return flow {
            emit(DataStatus.loading())
            when (apiServices.searchList(letter).code()) {
                in 200..202 -> {
                    emit(DataStatus.success(apiServices.searchList(letter).body()))
                }
            }
        }.catch { emit(DataStatus.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getFoodsByCategory(letter: String): Flow<DataStatus<FoodList>> {
        return flow {
            emit(DataStatus.loading())
            when (apiServices.filterList(letter).code()) {
                in 200..202 -> {
                    emit(DataStatus.success(apiServices.filterList(letter).body()))
                }
            }
        }.catch { emit(DataStatus.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getFoodsByArea(letter: String): Flow<DataStatus<FoodList>> {
        return flow {
            emit(DataStatus.loading())
            when (apiServices.filterList2(letter).code()) {
                in 200..202 -> {
                    emit(DataStatus.success(apiServices.filterList2(letter).body()))
                }
            }
        }.catch { emit(DataStatus.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun getFoodDetail(id: Int): Flow<DataStatus<FoodList>> {
        return flow {
            emit(DataStatus.loading())
            when (apiServices.getFoodDetails(id).code()) {
                in 200..202 -> {
                    emit(DataStatus.success(apiServices.getFoodDetails(id).body()))
                }
            }
        }.catch { emit(DataStatus.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }

    suspend fun saveFood(entity: FoodEntity) = dao.saveFood(entity)
    suspend fun deleteFood(entity: FoodEntity) = dao.deleteFood(entity)
    fun existsFood(id: Int) = dao.existsFood(id)
    fun getDbFoodList() = dao.getAllFoods()
}