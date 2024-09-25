package com.jery.feedchart.ui.details

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jery.feedchart.R
import com.jery.feedchart.data.model.ExpectedDailyGain
import com.jery.feedchart.data.model.FeedRecommendation
import com.jery.feedchart.util.composables.CustomStepSlider
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.VerticalIndicatorProperties

@Composable
fun BodyWeightScreen(feedRecommendations: List<FeedRecommendation>) {
    var selectedBodyWeight by rememberSaveable { mutableStateOf(feedRecommendations.first().bodyWeight) }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight()
    ) {
        BodyWeightSelector(
            recommendations = feedRecommendations,
            selectedBodyWeight = selectedBodyWeight,
            onBodyWeightSelected = { selectedBodyWeight = it }
        )
//        Spacer(modifier = Modifier.height(24.dp))
        ExpectedDailyGainDisplay(
            bodyWeight = selectedBodyWeight,
            feedRecommendations = feedRecommendations
        )
    }
}

@Composable
private fun BodyWeightSelector(
    recommendations: List<FeedRecommendation>,
    selectedBodyWeight: Int?,
    onBodyWeightSelected: (Int?) -> Unit,
) {
    val bodyWeightValues = recommendations.map { it.bodyWeight.toString() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Body Weight (kg)",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        CustomStepSlider(
            values = bodyWeightValues,
            selectedValue = selectedBodyWeight.toString(),
        ) { newValue ->
            onBodyWeightSelected(newValue.toInt())
        }
    }
}

@Composable
private fun ExpectedDailyGainDisplay(
    bodyWeight: Int?,
    feedRecommendations: List<FeedRecommendation>,
) {
    val recommendation = feedRecommendations.find { it.bodyWeight == bodyWeight }?.expectedDailyGain

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Expected Daily Gain (kg/day)",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        RecommendationChart(recommendation!!)
    }
}

@Composable
fun RecommendationChart(expectedDailyGain: ExpectedDailyGain) {
    val size = expectedDailyGain.semiIntensiveSystem.values.count()

    Box(
        modifier = Modifier.background(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                )
            ),
            shape = RoundedCornerShape(24.dp)
        ),
    ) {
        RowChart(
            data = expectedDailyGain.semiIntensiveSystem.keys.map { key ->
                Bars(
                    label = key,
                    values = listOf(
                        Bars.Data(
                            label = stringResource(R.string.intensive_system),
                            value = expectedDailyGain.intensiveSystem[key]!!.toDouble(),
                            color = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer))
                        ),
                        Bars.Data(
                            label = stringResource(R.string.semi_intensive_system),
                            value = expectedDailyGain.semiIntensiveSystem[key]!!.toDouble(),
                            color = Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.inversePrimary, MaterialTheme.colorScheme.secondaryContainer))
                        ),
                    ),
                )
            },
            barProperties = BarProperties(
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 8.dp, bottomRight = 8.dp),
                spacing = 4.dp,
                thickness = 32.dp
            ),
            labelProperties = LabelProperties(
                enabled = true,
                textStyle = MaterialTheme.typography.labelSmall.copy(textAlign = TextAlign.End),
            ),
            indicatorProperties = VerticalIndicatorProperties(enabled = true),
            labelHelperProperties = LabelHelperProperties(textStyle = MaterialTheme.typography.bodyMedium),
            dividerProperties = DividerProperties(enabled = false),
            gridProperties = GridProperties(enabled = false),
            animationMode = AnimationMode.Together(delayBuilder = { index -> (index * 10).toLong() }),
            animationDelay = 0,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            popupProperties = PopupProperties(
                enabled = true,
                duration = 3000,
                textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onPrimary),
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .height((size * 80 + 100).dp)
                .padding(16.dp),
        )
    }
}

@Composable
@Preview
fun RecommendationChartPreview() {
    val recommendation = ExpectedDailyGain(
        semiIntensiveSystem = mapOf("75" to 350, "100" to 370, "150" to 420, "200" to 460),
        intensiveSystem = mapOf("75" to 410, "100" to 430, "150" to 490, "200" to 540)
    )
    Box(
        modifier = Modifier.background(Color.White)
    ) {
        RecommendationChart(recommendation)
    }
}