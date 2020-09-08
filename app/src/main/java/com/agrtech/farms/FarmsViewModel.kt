package com.agrtech.farms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agrtech.repository.FarmsRepository

class FarmsViewModel(
    private val farmsRepository: FarmsRepository
) : ViewModel() {

    val farmsLiveData = MutableLiveData<List<Farm>>()

    fun loadFarms() {
        farmsLiveData.value = farmsRepository.getAll()
    }
}