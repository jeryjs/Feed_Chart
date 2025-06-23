package com.jery.feedchart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jery.feedchart.ui.details.DetailsActivity
import com.jery.feedchart.ui.home.HomeScreen
import com.jery.feedchart.ui.theme.FeedChartTheme
import com.jery.feedchart.util.LocaleUtils

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FeedChartTheme {
                HomeScreen { animalId ->
                    val intent = Intent(this, DetailsActivity::class.java)
                    intent.putExtra("ANIMAL_ID", animalId)
                    startActivity(intent)
                }
            }
        }
    }
}