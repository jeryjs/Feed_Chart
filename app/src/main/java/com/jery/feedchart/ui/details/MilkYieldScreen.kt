package com.jery.feedchart.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jery.feedchart.data.model.FeedDetails
import com.jery.feedchart.data.model.FeedRecommendation
import com.jery.feedchart.data.model.FodderAvailability
import com.jery.feedchart.util.composables.CustomStepSlider
import kotlin.collections.get

@Composable
fun MilkYieldScreen(feedRecommendations: List<FeedRecommendation>) {
    var selectedMilkYield by rememberSaveable { mutableStateOf(feedRecommendations.first().milkYield) }
    var selectedFodderAvailability by rememberSaveable { mutableStateOf(FodderAvailability.HIGH) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        MilkYieldSelector(
            recommendations = feedRecommendations,
            selectedMilkYield = selectedMilkYield,
            onMilkYieldSelected = { selectedMilkYield = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FodderAvailabilitySelector(
            selectedFodderAvailability = selectedFodderAvailability,
            onFodderAvailabilitySelected = { selectedFodderAvailability = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            text = "Milk Yield (lit/day)",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
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
    Text(
        text = "Green Fodder Availability",
        color = Color(0xFFB22222), // Red color
        fontWeight = FontWeight.Bold
    )

    RadioGroup(
        selectedOption = selectedFodderAvailability,
        onOptionSelected = { onFodderAvailabilitySelected(it) }
    )
}

@Composable
fun RadioGroup(selectedOption: FodderAvailability, onOptionSelected: (FodderAvailability) -> Unit) {
    Column {
        FodderAvailability.entries.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(option) }
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = option.name,
                    color = when (option) {
                        FodderAvailability.HIGH -> Color(0xFFFF0000) // High: Red
                        FodderAvailability.MODERATE -> Color(0xFF008000) // Moderate: Green
                        FodderAvailability.LOW -> Color(0xFF708090) // Low: Grey
                    }
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

    RecommendationChart(recommendation)
}

@Composable
fun RecommendationChart(recommendation: FeedDetails?) {
    recommendation?.let {
        Column {
            Text(
                text = "Concentrate : ${it.concentrateString} Kg",
                color = Color(0xFF0000FF), // Blue color
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .background(Color(0xFFD3D3D3), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Green Fodder : ${it.greenFodderString} Kg",
                color = Color(0xFF008000), // Green color
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .background(Color(0xFFD3D3D3), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dry Roughage : ${it.dryRoughageString} Kg",
                color = Color(0xFF4B0082), // Indigo color
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .background(Color(0xFFD3D3D3), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        }
    }
}