package com.agrtech.farms

import android.os.Parcelable
import com.agrtech.maps.WateringLevel
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
data class Farm(
    val farmId: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val location: LatLng,
    val areas: MutableList<Area>
) : Parcelable

@Parcelize
data class Area(
    val farmId: String,
    val points: List<LatLng>,
    val wateringLevel: WateringLevel
) : Parcelable