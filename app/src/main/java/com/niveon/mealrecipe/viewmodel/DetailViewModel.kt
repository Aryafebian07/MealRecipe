package com.niveon.mealrecipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niveon.mealrecipe.db.FoodEntity
import com.niveon.mealrecipe.model.FoodList
import com.niveon.mealrecipe.repository.MainRepository
import com.niveon.mealrecipe.util.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {
    private val _foodDetailData: MutableLiveData<DataStatus<FoodList>> = MutableLiveData()
    val foodDetailData: LiveData<DataStatus<FoodList>> get() = _foodDetailData
    val isSavedData = MutableLiveData<Boolean>()

    fun loadFoodDetail(id: Int) = viewModelScope.launch {
        repository.getFoodDetail(id).collect { _foodDetailData.value = it }
    }

    fun saveFood(entity: FoodEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveFood(entity)
    }

    fun deleteFood(entity: FoodEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFood(entity)
    }

    fun existsFood(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.existsFood(id).collect { isSavedData.postValue(it) }
    }
}