package com.jery.feedchart.data.model

import com.google.gson.annotations.SerializedName

data class ExpectedDailyGain(
    @SerializedName("semi_intensive_system")
    val semiIntensiveSystem: Map<String, Int>,
    @SerializedName("intensive_system")
    val intensiveSystem: Map<String, Int>
)
