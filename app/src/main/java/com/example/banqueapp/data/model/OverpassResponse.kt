package com.example.banqueapp.data.model

import com.google.gson.annotations.SerializedName

data class OverpassResponse(
    @SerializedName("version") val version: Double,
    @SerializedName("generator") val generator: String,
    @SerializedName("elements") val elements: List<OverpassElement>
)

data class OverpassElement(
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: Long,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("tags") val tags: OverpassTags?
)

data class OverpassTags(
    @SerializedName("amenity") val amenity: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("operator") val operator: String?,
    @SerializedName("atm") val atm: String?,
    @SerializedName("addr:street") val street: String?,
    @SerializedName("addr:housenumber") val houseNumber: String?,
    @SerializedName("addr:city") val city: String?,
    @SerializedName("addr:postcode") val postcode: String?,
    @SerializedName("opening_hours") val openingHours: String?,
    @SerializedName("currency:EUR") val currencyEur: String?,
    @SerializedName("wheelchair") val wheelchair: String?
)
