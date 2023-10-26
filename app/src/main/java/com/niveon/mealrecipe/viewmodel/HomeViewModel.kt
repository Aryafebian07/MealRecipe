package com.niveon.mealrecipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niveon.mealrecipe.model.AreaList
import com.niveon.mealrecipe.model.CategoryList
import com.niveon.mealrecipe.model.FoodList
import com.niveon.mealrecipe.repository.MainRepository
import com.niveon.mealrecipe.util.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _randomFood: MutableLiveData<List<FoodList.Meal>> = MutableLiveData()
    val randomFood: LiveData<List<FoodList.Meal>>  get() = _randomFood
    private val _categoriesList: MutableLiveData<DataStatus<CategoryList>> = MutableLiveData()
    val categoriesList: LiveData<DataStatus<CategoryList>> get() = _categoriesList
    val filtersListData = MutableLiveData<MutableList<Char>>()
    private val _foodList: MutableLiveData<DataStatus<FoodList>> = MutableLiveData()
    val foodList: LiveData<DataStatus<FoodList>> get() = _foodList
    private val _areaList: MutableLiveData<DataStatus<AreaList>> = MutableLiveData()
    val areaList: LiveData<DataStatus<AreaList>> get() = _areaList

    fun getRandomFood() = viewModelScope.launch {
        repository.getRandomFood().collect {
            _randomFood.value = it.body()?.meals!!
        }
    }

    fun getCategoriesList() = viewModelScope.launch {
        repository.getCategoriesList().collect {
            _categoriesList.value = it
        }
    }

    fun getAreasList() = viewModelScope.launch {
        repository.getAreasList().collect {
            _areaList.value = it
        }
    }

    fun loadFilterList() = viewModelScope.launch {
        val letters = listOf('A'..'Z').flatten().toMutableList()
        filtersListData.value = letters
    }

    fun getFoodsList(letter: String) = viewModelScope.launch {
        repository.getFoodsList(letter).collect {
            _foodList.value = it
        }
    }

    fun getFoodBySearch(letter: String) = viewModelScope.launch {
        repository.getFoodsBySearch(letter).collect {  _foodList.value = it }
    }

    fun getFoodByCategory(letter: String) = viewModelScope.launch {
        repository.getFoodsByCategory(letter).collect {  _foodList.value = it }
    }

    fun getFoodByArea(letter: String) = viewModelScope.launch {
        repository.getFoodsByArea(letter).collect {  _foodList.value = it }
    }
}