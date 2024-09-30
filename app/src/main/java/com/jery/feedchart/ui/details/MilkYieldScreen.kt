package com.jery.feedchart.ui.details

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jery.feedchart.R
import com.jery.feedchart.data.model.FeedDetails
import com.jery.feedchart.data.model.FeedRecommendation
import com.jery.feedchart.data.model.FodderAvailability
import com.jery.feedchart.data.repository.FeedRepository
import com.jery.feedchart.util.composables.CustomPieChart
import com.jery.feedchart.util.composables.CustomStepSlider
import kotlin.collections.get

@Composable
fun MilkYieldScreen(feedRecommendations: List<FeedRecommendation>) {
    val context = LocalContext.current
    val feedRecHash = feedRecommendations.hashCode().toString()
    val sharedPreferences = context.getSharedPreferences("feed_prefs", Context.MODE_PRIVATE)
    var selectedMilkYield by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getFloat(
                "selected_milk_yield_$feedRecHash",
                feedRecommendations.last().milkYield!!
            )
        )
    }
    var selectedFodderAvailability by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getString(
                "selected_fodder_availability_$feedRecHash",
                FodderAvailability.HIGH.name
            )?.let { FodderAvailability.valueOf(it) } ?: FodderAvailability.LOW)
    }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxHeight()
    ) {
        MilkYieldSelector(
            recommendations = feedRecommendations,
            selectedMilkYield = selectedMilkYield,
            onMilkYieldSelected = {
                sharedPreferences.edit().putFloat("selected_milk_yield_$feedRecHash", it!!).apply()
                selectedMilkYield = it
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        FodderAvailabilitySelector(
            selectedFodderAvailability = selectedFodderAvailability,
            onFodderAvailabilitySelected = {
                sharedPreferences.edit()
                    .putString("selected_fodder_availability_$feedRecHash", it.name).apply()
                selectedFodderAvailability = it
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        FeedRecommendationDisplay(
            milkYield = selectedMilkYield,
            selectedFodderAvailability = selectedFodderAvailability,
            feedRecommendations = feedRecommendations
        )
    }
}

@Composable
private fun MilkYieldSelector(
    recommendations: List<FeedRecommendation>,
    selectedMilkYield: Float?,
    onMilkYieldSelected: (Float?) -> Unit,
) {
    val milkYieldValues = recommendations.map { it.milkYield.toString() }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.milk_yield_lit_day),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomStepSlider(
            values = milkYieldValues,
            selectedValue = (selectedMilkYield ?: milkYieldValues.first()).toString()
        ) { newValue ->
            onMilkYieldSelected(newValue.toFloat())
        }
    }
}

@Composable
fun FodderAvailabilitySelector(
    selectedFodderAvailability: FodderAvailability,
    onFodderAvailabilitySelected: (FodderAvailability) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.green_fodder_availability),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))

        RadioGroup(
            selectedOption = selectedFodderAvailability,
            onOptionSelected = { onFodderAvailabilitySelected(it) }
        )
    }
}

@Composable
fun RadioGroup(selectedOption: FodderAvailability, onOptionSelected: (FodderAvailability) -> Unit) {
    val density = LocalContext.current.resources.displayMetrics.density
    val screenWidth = LocalConfiguration.current.screenWidthDp * density/4

    // Adaptive font scaling factor based on both width and height
    val fontScalingFactor = (screenWidth * 0.07f)
    val selectedFontSize by animateFloatAsState(targetValue = fontScalingFactor)
    val unselectedFontSize = selectedFontSize * 0.7f

    Row {
        FodderAvailability.entries.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onOptionSelected(option) }
                    .animateContentSize()
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = option.name,
                    fontSize = with(density) { if (option == selectedOption) selectedFontSize.sp else unselectedFontSize.sp },
                    style = MaterialTheme.typography.labelSmall,
                    color = when (option) {
                        FodderAvailability.HIGH -> Color(0xFFFF0000) // High: Red
                        FodderAvailability.MODERATE -> Color(0xFF008000) // Moderate: Green
                        FodderAvailability.LOW -> Color(0xFF5F89B4) // Low: Grey
                    }.copy(alpha = if (option == selectedOption) 1f else 0.6f),
                    maxLines = 1,
                    modifier = Modifier.animateContentSize()
                )
            }
        }
    }
}

@Composable
private fun FeedRecommendationDisplay(
    milkYield: Float?,
    selectedFodderAvailability: FodderAvailability?,
    feedRecommendations: List<FeedRecommendation>,
) {
    val recommendation =
        feedRecommendations.find { it.milkYield == milkYield }?.greenFodderAvailability?.get(
            selectedFodderAvailability
        )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        Text(
            text = stringResource(R.string.feed_recommendation_per_day),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        RecommendationChart(recommendation)
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun RecommendationChart(recommendation: FeedDetails?) {
    recommendation?.let {
        var pieData = listOf(
            stringResource(R.string.concentrate), it.concentrate, Color(0xFFFFEB3B).copy(alpha = 0.6f),
            stringResource(R.string.green_fodder), it.greenFodder, Color(0xFF4CAF50).copy(alpha = 0.6f),
            stringResource(R.string.dry_roughage), it.dryRoughage, Color(0xFF8D6E63).copy(alpha = 0.6f),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .animateContentSize(),
        ) {
            CustomPieChart(
                pieData = pieData,
                showLabelsInArcs = true,
                valueLabelFormatter = { "%.2f Kg".format(it) },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


@Preview
@Composable
fun PreviewMilkYieldScreen() {
    Box(
        modifier = Modifier.background(Color.White)
    ) {
        MilkYieldScreen(feedRecommendations = FeedRepository(LocalContext.current).getRecommendations().entries.first().value)
    }
}