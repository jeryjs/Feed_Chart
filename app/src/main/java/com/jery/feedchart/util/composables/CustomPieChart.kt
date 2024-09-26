package com.jery.feedchart.util.composables

import android.graphics.Paint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
@Preview
fun CustomPieChartPreview() {
    val pieData = listOf(
        "Concentrate", 20f, Color.Red,
        "Green Fodder", 30f, Color.Blue,
        "Dry Roughage", 50f, Color.Green
    )

    CustomPieChart(
        pieData = pieData,
        animDuration = 1000,
        showLabelsInArcs = true
    )
}

@Composable
fun CustomPieChart(
    pieData: List<Any>,  // List containing labels, values, and colors in sequence
    modifier: Modifier = Modifier,
    chartBarWidth: Dp = 65.dp,
    animDuration: Int = 1000,
    maxArcPercentage: Float = 1f,  // Max size each arc can take up (1f = 100%)
    showLabelsInArcs: Boolean = false,  // Option to show labels inside arcs
    customLabelStyle: @Composable (String) -> Unit = { label -> DefaultLabel(label) },
    customValueStyle: @Composable (String) -> Unit = { value -> DefaultValue(value) },
    rotateOnValueChange: Boolean = true, // Rotate on value change
    valueLabelFormatter: (Float) -> String = { "%.2f".format(it) }  // Custom label for arc values
) {
    val labels = pieData.filterIndexed { index, _ -> index % 3 == 0 } as List<String>
    val values = pieData.filterIndexed { index, _ -> index % 3 == 1 } as List<Float>
    val colors = pieData.filterIndexed { index, _ -> index % 3 == 2 } as List<Color>

    val radiusOuter: Dp = 250.dp

    val totalSum = values.sum()
    val floatValue = values.map {
        val adjustedValue = 360 * it / totalSum
        adjustedValue.coerceAtMost(360 * maxArcPercentage)
    }

    var animationPlayed by remember { mutableStateOf(false) }
    var lastValue = 0f

    // Animation setup for rotation
    val rotationAngle = remember { Animatable(0f) }

    if (rotateOnValueChange) {
        LaunchedEffect(pieData) {
            rotationAngle.snapTo(0f) // Reset the rotation to 0 before the new rotation
            rotationAngle.animateTo(
                targetValue = rotationAngle.value + 360f,
                animationSpec = tween(durationMillis = animDuration)
            )
        }

    }

    val animateRadius by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    LaunchedEffect(key1 = pieData) {
        animationPlayed = true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Display the details below the pie chart
        DetailsPieChart(
            labels = labels,
            values = values,
            colors = colors,
            customLabelStyle = customLabelStyle,
            customValueStyle = customValueStyle
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Pie Chart with Canvas and rotation
        Box(
            modifier = Modifier
                .size((animateRadius).dp)
                .rotate(rotationAngle.value),  // Rotate the chart
            contentAlignment = Alignment.Center
        ) {
            val textColor = MaterialTheme.colorScheme.onSurface
            Canvas(modifier = Modifier.size((animateRadius).dp)) {
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        startAngle = lastValue,
                        sweepAngle = value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )

                    if (showLabelsInArcs) {
                        // Calculate midpoint of the arc and display custom value
                        val midAngle = lastValue + value / 2
                        val angleRadians = Math.toRadians(midAngle.toDouble())

                        // Proper calculation for arc value position
                        val labelRadius = (animateRadius * 4.2 - chartBarWidth.toPx() * 4.2)
                        val x = (size.center.x + labelRadius * cos(angleRadians)).toFloat()
                        val y = (size.center.y + labelRadius * sin(angleRadians)).toFloat()


                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                valueLabelFormatter(values[index]),  // Show custom value
                                x,
                                y,
                                Paint().apply {
                                    color = textColor.toArgb()
                                    textAlign = Paint.Align.CENTER
                                    isFakeBoldText = true
                                    textSize = 32f
                                }
                            )
                        }
                    }

                    lastValue += value
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsPieChart(
    labels: List<String>,
    values: List<Float>,
    colors: List<Color>,
    customLabelStyle: @Composable (String) -> Unit,
    customValueStyle: @Composable (String) -> Unit
) {
    FlowRow (
        modifier = Modifier.fillMaxWidth()
    ) {
        // Display each item in the pie chart
        labels.forEachIndexed { index, label ->
            DetailsPieChartItem(
                label = label,
                value = values[index],
                color = colors[index],
                customLabelStyle = customLabelStyle,
                customValueStyle = customValueStyle
            )
        }
    }
}

@Composable
fun DetailsPieChartItem(
    label: String,
    value: Float,
    height: Dp = 16.dp,
    color: Color,
    customLabelStyle: @Composable (String) -> Unit,
    customValueStyle: @Composable (String) -> Unit
) {
    Surface(
        modifier = Modifier.padding(horizontal = 10.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = color,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .size(height)
            )

            Column(modifier = Modifier) {
                customLabelStyle(label)
//                customValueStyle("%.2f".format(value))
            }
        }
    }
}

@Composable
fun DefaultLabel(label: String) {
    Text(
        modifier = Modifier.padding(start = 12.dp),
        text = label,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun DefaultValue(value: String) {
    Text(
        modifier = Modifier.padding(start = 15.dp),
        text = value,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        color = Color.Gray
    )
}