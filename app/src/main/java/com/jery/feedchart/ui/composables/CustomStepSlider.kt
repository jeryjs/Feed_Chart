package com.jery.feedchart.ui.composables

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomStepSlider(
    modifier: Modifier = Modifier,
    values: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current

    // Scale the size based on screen dimensions
    val sliderHeight = with(density) { screenHeight * 0.15f }  // Scale height by 15% of screen height
    val thumbSize = with(density) { screenWidth * 0.17f }       // Thumb size 15% of screen width
    val fontSizeScalingFactor = with(density) { (screenWidth * 0.015f) } // Font size scaling factor

    // Step setup
    val stepCount = values.size - 1
    val currentStep = values.indexOf(selectedValue).coerceIn(0, stepCount)
    val sliderPosition = remember { mutableStateOf(currentStep.toFloat()) }

    // Animated slider position
    val animatedSliderPosition by animateFloatAsState(
        targetValue = sliderPosition.value,
        animationSpec = tween(
            durationMillis = 100,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize() // Adding animation to size changes
    ) {
        // Slider Box with animated background
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(sliderHeight) // Scaled height
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .animateContentSize()
        ) {
            Slider(
                value = animatedSliderPosition,
                valueRange = 0f..stepCount.toFloat(),
                steps = stepCount - 1,
                onValueChange = { newPosition ->
                    sliderPosition.value = newPosition
                    val selectedIndex = newPosition.roundToInt().coerceIn(0, stepCount)
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
                    .height(sliderHeight * 1.25f),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(thumbSize)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                CircleShape
                            )
                    )
                }
            )
            // Row with labels, with animation to text and font size
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                values.forEach { value ->
                    // Label animations
                    val selected = value == selectedValue
                    val fontSize by animateFloatAsState(
                        targetValue = with(density) { if (selected)  fontSizeScalingFactor.toPx() * 1.5f  else  fontSizeScalingFactor.toPx() * 1.0f },
                        animationSpec = tween(durationMillis = 500)
                    )
                    val labelColor by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                        animationSpec = tween(durationMillis = 500)
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(screenWidth * 0.15f)
                            .clip(CircleShape)
                            .animateContentSize()
                    ) {
                        Text(
                            text = value,
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = labelColor,
                            maxLines = 1,
                            modifier = Modifier.animateContentSize()
                        )
                    }
                }
            }
        }
    }
}



class SampleValuesProvider : PreviewParameterProvider<List<Any>> {
    override val values: Sequence<List<Any>> =
        sequenceOf(listOf("5.0", "7.5", "10.0", "12.5", "15.0", "20.0"))
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