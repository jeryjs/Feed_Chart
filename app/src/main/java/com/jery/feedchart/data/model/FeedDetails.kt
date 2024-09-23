package com.jery.feedchart.data.model

import com.google.gson.annotations.SerializedName
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
