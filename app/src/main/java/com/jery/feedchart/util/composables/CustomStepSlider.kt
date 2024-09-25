package com.jery.feedchart.util.composables

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomStepSlider(
    modifier: Modifier = Modifier,
    values: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
) {
    // Convert values into a step index for slider control
    val stepCount = values.size - 1
    val currentStep = values.indexOf(selectedValue)
    val sliderPosition = remember { mutableStateOf(currentStep.toFloat()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Custom Slider with Steps
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Slider(
                value = sliderPosition.value,
                valueRange = 0f..stepCount.toFloat(),
                steps = stepCount - 1,
                onValueChange = { newPosition ->
                    sliderPosition.value = newPosition
                    val selectedIndex = newPosition.toInt().coerceIn(0, stepCount)
                    onValueSelected(values[selectedIndex])
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                    )
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
            ) {
                values.forEach { value ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(58.dp)    // Same size as thumb to ensure the text is aligned inside it.
                            .clip(CircleShape)
                    ) {
                        Text(
                            text = value.toString(),
                            fontSize = if (value == selectedValue) 20.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (value == selectedValue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceBright,
                            modifier = Modifier.animateContentSize()
                        )
                    }
                }
            }
        }
    }
}

class SampleValuesProvider : PreviewParameterProvider<List<Any>> {
    override val values: Sequence<List<Any>> = sequenceOf(listOf("5.0", "7.5", "10.0", "12.5", "15.0", "20.0"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewCustomStepSlider(
    @PreviewParameter(SampleValuesProvider::class) values: List<String>,
) {
    CustomStepSlider(
        values = values,
        selectedValue = values[3],
        onValueSelected = {},
    )
}