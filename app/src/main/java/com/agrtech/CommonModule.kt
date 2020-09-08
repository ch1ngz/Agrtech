package com.agrtech

import com.agrtech.farms.FarmsViewModel
import com.agrtech.maps.MapsViewModel
import com.agrtech.repository.FarmsRepository
import com.agrtech.repository.FarmsRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

object CommonModule {

    fun create() = module {
        single { FarmsRepositoryImpl() } bind FarmsRepository::class
        viewModel { FarmsViewModel(get()) }
        viewModel { MapsViewModel(get()) }
    }
}