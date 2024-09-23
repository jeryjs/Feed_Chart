package com.jery.feedchart.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jery.feedchart.R
import com.jery.feedchart.data.model.FeedRecommendation

class FeedRepository(val context: Context) {

    fun getRecommendations(): Map<Int, List<FeedRecommendation>> {
        val json = context.resources.openRawResource(R.raw.feed_recommendations).bufferedReader().use { it.readText() }
        val feedRecommendationType = object : TypeToken<Map<Int, List<FeedRecommendation>>>() {}.type
        return Gson().fromJson(json, feedRecommendationType)
    }
}