package com.agrtech.repository

import com.agrtech.farms.Area
import com.agrtech.farms.Farm
import com.google.android.gms.maps.model.LatLng

interface FarmsRepository {
    fun saveArea(area: Area)
    fun getAll(): List<Farm>
}

class FarmsRepositoryImpl : FarmsRepository {

    private val farms: MutableList<Farm> = mutableListOf()

    init {
        farms.addAll(getStubFarms())
    }

    override fun getAll(): List<Farm> = farms

    override fun saveArea(area: Area) {
        val farm = farms.find { it.farmId == area.farmId } ?: return
        farm.areas.add(area)
    }

    private fun getStubFarms() = listOf(
        Farm(
            name = "Moo Valley",
            address = "https://www.google.ru/maps/place/Moo+Valley+Farm/@35.8701082,-85.4082868,805m/data=!3m1!1e3!4m8!1m2!2m1!1sfarm!3m4!1s0x0:0x9abd18699797e2bc!8m2!3d35.8702557!4d-85.4102254",
            location = LatLng(35.8701082, -85.4082868),
            areas = mutableListOf()
        ),
        Farm(
            name = "Royal Inn",
            address = "https://www.google.ru/maps/place/Royal+Inn/@35.951408,-85.4951079,7255m/data=!3m1!1e3!4m17!1m8!2m7!1sHotels!3m5!1sHotels!2s35.872906,+-85.420126!4m2!1d-85.4201264!2d35.8729056!3m7!1s0x88672efa3fc09e69:0x32258cad5ed28c82!5m2!4m1!1i2!8m2!3d35.951408!4d-85.4852815",
            location = LatLng(35.951408,-85.4951079),
            areas = mutableListOf()
        )
    )
}