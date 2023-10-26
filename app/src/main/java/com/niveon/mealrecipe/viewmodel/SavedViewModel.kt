package com.niveon.mealrecipe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niveon.mealrecipe.db.FoodEntity
import com.niveon.mealrecipe.repository.MainRepository
import com.niveon.mealrecipe.util.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {
    private val _foodList: MutableLiveData<DataStatus<List<FoodEntity>>> = MutableLiveData()
    val foodList: LiveData<DataStatus<List<FoodEntity>>> get() = _foodList

    fun getSavedFoodList() = viewModelScope.launch {
        repository.getDbFoodList().collect {
            _foodList.value=DataStatus.success(it)
        }
    }
}