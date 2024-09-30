package com.jery.feedchart.ui.details

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.jery.feedchart.data.model.ExpectedDailyGain
import com.jery.feedchart.data.model.FeedRecommendation
import com.jery.feedchart.util.composables.CustomRowChart
import com.jery.feedchart.util.composables.CustomStepSlider
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
    val context = LocalContext.current
    val feedRecHash = feedRecommendations.hashCode().toString()
    val sharedPreferences = context.getSharedPreferences("feed_prefs", Context.MODE_PRIVATE)
    var selectedBodyWeight by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getInt(
                "selected_body_weight_$feedRecHash",
                feedRecommendations.first().bodyWeight!!
            )
        )
    }
    var selectedSystemType by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getInt("selected_system_type_$feedRecHash", 0)
        )
    }

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
//            .fillMaxHeight()
    ) {
        BodyWeightSelector(
            recommendations = feedRecommendations,
            selectedBodyWeight = selectedBodyWeight,
            onBodyWeightSelected = {
                sharedPreferences.edit().putInt("selected_body_weight_$feedRecHash", it!!).apply()
                selectedBodyWeight = it
            }
        )
        Spacer(modifier = Modifier.height(64.dp))
        ExpectedDailyGainDisplay(
            bodyWeight = selectedBodyWeight,
            feedRecommendations = feedRecommendations,
            selectedSystemType = selectedSystemType
        )
        Spacer(modifier = Modifier.height(12.dp))
        SystemTypeSelector(
            options = listOf(stringResource(R.string.intensive_system), stringResource(R.string.semi_intensive_system)),
            selectedOption = selectedSystemType,
        ) {
            sharedPreferences.edit().putInt("selected_system_type_$feedRecHash", it).apply()
            selectedSystemType = it
        }
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
            text = stringResource(R.string.body_weight_kg),
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
    selectedSystemType: Int
) {
    val recommendation = feedRecommendations.find { it.bodyWeight == bodyWeight }?.expectedDailyGain

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.expected_daily_gain_kg_day),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        RecommendationChart(recommendation!!, selectedSystemType)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RecommendationChart(expectedDailyGain: ExpectedDailyGain, selectedSystemType: Int) {
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
        val res = LocalContext.current.resources
        CustomRowChart(
            data = expectedDailyGain.semiIntensiveSystem.keys.map { key ->
                Bars(
                    label = key,
                    values = listOf(
                        Bars.Data(
                            label = stringResource(if (selectedSystemType == 0) R.string.intensive_system else R.string.semi_intensive_system),
                            value = (if (selectedSystemType == 0) expectedDailyGain.intensiveSystem[key] else expectedDailyGain.semiIntensiveSystem[key])!!.toDouble(),
                            color = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.inversePrimary,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        )
                    ),
                )
            },
            barProperties = BarProperties(
                cornerRadius = Bars.Data.Radius.Rectangle(topRight = 8.dp, bottomRight = 8.dp),
                spacing = 4.dp,
                thickness = 32.dp
            ),
            labelProperties = LabelProperties(enabled = true, textStyle = MaterialTheme.typography.labelSmall.copy(textAlign = TextAlign.End, color = MaterialTheme.colorScheme.primary)),
            barOverlayText = { label, value -> "${res.getString(if (label == res.getString(R.string.intensive_system)) R.string.intensive else R.string.semi_intensive)} - $value gm" },
            barOverlayStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)),
            indicatorProperties = VerticalIndicatorProperties(enabled = false),
            labelHelperProperties = LabelHelperProperties(enabled = false),
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
                textStyle = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)),
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .height((size * 60).dp)
                .padding(16.dp),
        )
    }
}

@Composable
fun SystemTypeSelector(options: List<String>, selectedOption: Int, onOptionSelected: (Int) -> Unit) {
    val density = LocalContext.current.resources.displayMetrics.density
    val screenWidth = LocalConfiguration.current.screenWidthDp * density/4
    // Adaptive font scaling factor based on both width and height
    val fontScalingFactor = (screenWidth * 0.06f)
    val selectedFontSize by animateFloatAsState(targetValue = fontScalingFactor)
    val unselectedFontSize = selectedFontSize * 0.7f

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = { onOptionSelected(options.indexOf(option)) })
                    .animateContentSize()
                    .weight(1f),
            ) {
                RadioButton(
                    selected = option == options[selectedOption],
                    onClick = { onOptionSelected(options.indexOf(option)) },
                )
                Text(
                    text = option,
                    fontSize = with(density) { if (option == options[selectedOption]) selectedFontSize.sp else unselectedFontSize.sp },
                    style = MaterialTheme.typography.labelSmall,
                    color = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)[options.indexOf(option)],
                )
            }
        }
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
        RecommendationChart(recommendation, 0)
    }
}