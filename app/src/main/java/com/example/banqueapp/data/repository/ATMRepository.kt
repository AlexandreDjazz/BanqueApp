package com.example.banqueapp.data.repository

import com.example.banqueapp.data.api.OverpassApiService
import com.example.banqueapp.data.model.ATM
import com.example.banqueapp.data.model.OverpassElement
import com.google.android.gms.maps.model.LatLng
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ATMRepository {

    private val apiService: OverpassApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(OverpassApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(OverpassApiService::class.java)
    }

    suspend fun getNearbyATMs(userLocation: LatLng, radiusMeters: Int = 2000): Result<List<ATM>> {
        return try {
            val query = OverpassApiService.buildATMQuery(
                lat = userLocation.latitude,
                lon = userLocation.longitude,
                radiusMeters = radiusMeters
            )

            val response = apiService.getATMs(query)

            if (response.isSuccessful && response.body() != null) {
                val atmList = response.body()!!.elements.mapNotNull { element ->
                    convertToATM(element, userLocation)
                }.sortedBy { it.distance }

                Result.success(atmList)
            } else {
                Result.failure(Exception("Erreur API: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun convertToATM(element: OverpassElement, userLocation: LatLng): ATM? {

        if (element.lat == 0.0 && element.lon == 0.0) return null

        val position = LatLng(element.lat, element.lon)
        val tags = element.tags
        val name = buildATMName(tags?.name, tags?.operator)
        val address = buildAddress(tags)
        val isAvailable = true
        val services = buildServices(tags)
        val distance = calculateDistance(userLocation, position)

        return ATM(
            id = element.id.toString(),
            name = name,
            address = address,
            position = position,
            distance = distance,
            isAvailable = isAvailable,
            services = services
        )
    }

    private fun buildATMName(name: String?, operator: String?): String {
        return when {
            !name.isNullOrBlank() -> name
            !operator.isNullOrBlank() -> "ATM $operator"
            else -> "Distributeur automatique"
        }
    }

    private fun buildAddress(tags: com.example.banqueapp.data.model.OverpassTags?): String {
        if (tags == null) return "Adresse non disponible"

        val parts = mutableListOf<String>()

        if (!tags.houseNumber.isNullOrBlank() && !tags.street.isNullOrBlank()) {
            parts.add("${tags.houseNumber} ${tags.street}")
        } else if (!tags.street.isNullOrBlank()) {
            parts.add(tags.street)
        }

        val cityPart = mutableListOf<String>()
        if (!tags.postcode.isNullOrBlank()) cityPart.add(tags.postcode)
        if (!tags.city.isNullOrBlank()) cityPart.add(tags.city)
        if (cityPart.isNotEmpty()) parts.add(cityPart.joinToString(" "))

        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            "Adresse non disponible"
        }
    }

    private fun buildServices(tags: com.example.banqueapp.data.model.OverpassTags?): List<String> {
        val services = mutableListOf<String>()

        tags?.let {
            services.add("Retrait")

            if (it.openingHours?.contains("24/7") == true) {
                services.add("24h/24")
            }

            if (it.wheelchair == "yes") {
                services.add("Accessible PMR")
            }

            if (it.currencyEur == "yes") {
                services.add("EUR")
            }
        }

        return services
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val earthRadius = 6371.0

        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(start.latitude)) * cos(Math.toRadians(end.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}
