package com.jery.feedchart.data.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.jery.feedchart.R
import kotlin.math.floor

data class FeedDetails(
    @SerializedName("concentrate")
    val concentrate: Float,
    @SerializedName("green_fodder")
    val greenFodder: Float,
    @SerializedName("dry_roughage")
    val dryRoughage: Float
) {
    private fun Float.toIntIfNoDecimals(): String {
        return if (this == floor(this)) this.toInt().toString() else this.toString()
    }

    val concentrateString get() = concentrate.toIntIfNoDecimals()
    val greenFodderString get() = greenFodder.toIntIfNoDecimals()
    val dryRoughageString get() = dryRoughage.toIntIfNoDecimals()
}

enum class FodderAvailability(@StringRes val stringResId: Int) {
    @SerializedName("high")
    HIGH(R.string.high),
    @SerializedName("moderate")
    MODERATE(R.string.moderate),
    @SerializedName("low")
    LOW(R.string.low)
}