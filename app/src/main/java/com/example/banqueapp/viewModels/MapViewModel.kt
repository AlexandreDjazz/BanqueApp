package com.example.banqueapp.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banqueapp.data.model.ATM
import com.example.banqueapp.data.repository.ATMRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class MapUiState(
    val currentLocation: LatLng? = null,
    val atmList: List<ATM> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasLocationPermission: Boolean = false
)

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val atmRepository = ATMRepository()

    fun initializeLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        checkLocationPermission(context)
    }

    private fun checkLocationPermission(context: Context) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        _uiState.value = _uiState.value.copy(hasLocationPermission = hasPermission)
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(hasLocationPermission = granted)
        if (granted) {
            getCurrentLocation()
        }
    }

    @Suppress("MissingPermission")
    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val location = getLocationAsync()

                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    _uiState.value = _uiState.value.copy(
                        currentLocation = currentLatLng,
                        isLoading = false,
                        errorMessage = null
                    )
                    loadNearbyATMs(currentLatLng)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Impossible d'obtenir votre position"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erreur: ${e.message}"
                )
            }
        }
    }

    @Suppress("MissingPermission")
    private suspend fun getLocationAsync(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient?.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )?.addOnSuccessListener { location ->
                continuation.resume(location)
            }?.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }

    private fun loadNearbyATMs(userLocation: LatLng) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val result = atmRepository.getNearbyATMs(
                    userLocation = userLocation,
                    radiusMeters = 2000
                )

                result.fold(
                    onSuccess = { atmList ->
                        _uiState.value = _uiState.value.copy(
                            atmList = atmList,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            atmList = emptyList(),
                            isLoading = false,
                            errorMessage = "Erreur lors du chargement des ATM: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    atmList = emptyList(),
                    isLoading = false,
                    errorMessage = "Erreur: ${e.message}"
                )
            }
        }
    }

    fun refreshATMs() {
        _uiState.value.currentLocation?.let { location ->
            loadNearbyATMs(location)
        }
    }
}
