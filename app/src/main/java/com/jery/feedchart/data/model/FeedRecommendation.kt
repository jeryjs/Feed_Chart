package com.jery.feedchart.data.model

import com.google.gson.annotations.SerializedName

data class FeedRecommendation(
    @SerializedName("milk_yield")
    val milkYield: Float?,
    @SerializedName("body_weight")
    val bodyWeight: Float?,
    @SerializedName("green_fodder_availability")
    val greenFodderAvailability: Map<FodderAvailability, FeedDetails>?,
    @SerializedName("expected_daily_gain")
    val expectedDailyGain: ExpectedDailyGain? // New for sheep
)

data class ExpectedDailyGain(
    @SerializedName("semi_intensive_system")
    val semiIntensiveSystem: Map<Int, Int>,
    @SerializedName("intensive_system")
    val intensiveSystem: Map<Int, Int>
)

enum class FodderAvailability {
    @SerializedName("high")
    HIGH,
    @SerializedName("moderate")
    MODERATE,
    @SerializedName("low")
    LOW
}