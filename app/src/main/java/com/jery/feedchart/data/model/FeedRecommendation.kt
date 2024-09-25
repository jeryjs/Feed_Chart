package com.jery.feedchart.data.model

import com.google.gson.annotations.SerializedName

data class FeedRecommendation(
    @SerializedName("type")
    val displayType: Int,

    @SerializedName("milk_yield")
    val milkYield: Float?,

    @SerializedName("body_weight")
    val bodyWeight: Int?,

    @SerializedName("green_fodder_availability")
    val greenFodderAvailability: Map<FodderAvailability, FeedDetails>?,

    @SerializedName("expected_daily_gain")
    val expectedDailyGain: ExpectedDailyGain?,
)