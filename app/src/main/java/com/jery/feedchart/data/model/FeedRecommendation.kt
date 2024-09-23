package com.jery.feedchart.data.model

import com.google.gson.annotations.SerializedName

data class FeedRecommendation(
    @SerializedName("milk_yield")
    val milkYield: Float,
    @SerializedName("green_fodder_availability")
    val greenFodderAvailability: Map<FodderAvailability, FeedDetails>
)

enum class FodderAvailability {
    @SerializedName("high")
    HIGH,
    @SerializedName("moderate")
    MODERATE,
    @SerializedName("low")
    LOW
}