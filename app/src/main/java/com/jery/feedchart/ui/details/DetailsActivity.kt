package com.jery.feedchart.ui.details

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jery.feedchart.R
import com.jery.feedchart.data.repository.FeedRepository
import com.jery.feedchart.ui.theme.FeedChartTheme
import com.jery.feedchart.util.composables.BottomLanguageBar
import java.util.Locale

class DetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val animalId = intent.getIntExtra("ANIMAL_ID", -1)
        val feedRecommendations = FeedRepository(this).getRecommendations()[animalId] ?: emptyList()

        setContent {
            FeedChartTheme {
                Scaffold(
                    topBar = { MyAppBar(animalId) },
                    bottomBar = { BottomLanguageBar() },
                ) { paddingValues ->
                    Box(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        when (feedRecommendations.first().displayType) {
                            0 -> MilkYieldScreen(feedRecommendations)
                            1 -> BodyWeightScreen(feedRecommendations)
                            else -> null
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun MyAppBar(animalId: Int = 0) {
    val activity = LocalContext.current as Activity
    var showExtendedMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.animateContentSize()) {
        TopAppBar(
            title = { Text(text = stringArrayResource(R.array.animal_desc)[animalId]) },
            navigationIcon = {
                IconButton(onClick = { activity.finish() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Go Back"
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                IconButton(onClick = { showExtendedMenu = !showExtendedMenu }) {
                    Icon(
                        painter = rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.anim_caret_down), !showExtendedMenu),
                        contentDescription = "Extended Options",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        )
        if (showExtendedMenu)
            ActionChips()
    }
}

@Composable
fun ActionChips() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionChip(text = stringResource(R.string.details))
        ActionChip(text = stringResource(R.string.request_form))
        ActionChip(text = stringResource(R.string.feedback))
    }
}

@Composable
fun ActionChip(text: String) {
    AssistChip(
        onClick = { /*TODO*/ },
        label = {
            Text(
                text = text.uppercase(Locale.getDefault()),
                fontSize = 14.sp
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primary,
            labelColor = Color.White
        ),
        modifier = Modifier.padding(4.dp)
    )
}
