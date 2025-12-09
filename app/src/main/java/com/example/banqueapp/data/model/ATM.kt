package com.example.banqueapp.data.model

import com.google.android.gms.maps.model.LatLng

data class ATM(
    val id: String,
    val name: String,
    val address: String,
    val position: LatLng,
    val distance: Double = 0.0,
    val isAvailable: Boolean = true,
    val services: List<String> = emptyList()
)
