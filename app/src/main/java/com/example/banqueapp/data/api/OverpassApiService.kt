package com.example.banqueapp.data.api

import com.example.banqueapp.data.model.OverpassResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassApiService {

    @GET("interpreter")
    suspend fun getATMs(@Query("data") data: String): Response<OverpassResponse>

    companion object {
        const val BASE_URL = "https://overpass-api.de/api/"

        fun buildATMQuery(lat: Double, lon: Double, radiusMeters: Int = 5000): String {
            return """
                [out:json][timeout:25];
                (
                  node["amenity"="atm"](around:$radiusMeters,$lat,$lon);
                  node["amenity"="bank"]["atm"="yes"](around:$radiusMeters,$lat,$lon);
                );
                out body;
            """.trimIndent()
        }
    }
}
