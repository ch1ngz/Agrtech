package com.agrtech.maps

import androidx.lifecycle.ViewModel
import com.agrtech.farms.Area
import com.agrtech.repository.FarmsRepository
import com.google.android.gms.maps.model.LatLng

class MapsViewModel(
    private val farmsRepository: FarmsRepository
) : ViewModel() {

    fun saveArea(farmId: String, bounds: List<LatLng>, level: WateringLevel) {
        val area = Area(farmId, bounds, level)
        farmsRepository.saveArea(area)
    }
}