package com.jery.feedchart.util.composables

import android.content.Context
import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jery.feedchart.R
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
@Preview
private fun CustomPieChartPreview() {
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

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun CustomPieChart(
    pieData: List<Any>,
    modifier: Modifier = Modifier,
    chartBarWidth: Dp = 65.dp,
    animDuration: Int = 700,
    radiusOuter: Dp = 225.dp,
    valueLabelFormatter: (Float) -> String = { "%.2f".format(it) },
    showLabelsInArcs: Boolean = false,
    showValuesBelowChart: Boolean = true,
    customLabelStyle: @Composable (String) -> Unit = { label -> DefaultLabel(label) },
    customValueStyle: @Composable (String) -> Unit = { value ->
        DefaultValue(
            valueLabelFormatter(
                value.toFloat()
            ).toString()
        )
    },
    rotateOnValueChange: Boolean = false,
) {
    val labels = pieData.filterIndexed { index, _ -> index % 3 == 0 } as List<String>
    val values = pieData.filterIndexed { index, _ -> index % 3 == 1 } as List<Float>
    val colors = pieData.filterIndexed { index, _ -> index % 3 == 2 } as List<Color>

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("pie_chart_prefs", Context.MODE_PRIVATE)
    val scrollState = rememberScrollState()

    val totalSum = values.sum()
    val adjustedValues = values.map { 360 * it / totalSum }

    var animationPlayed by remember { mutableStateOf(false) }
    var lastValue = 0f

    // Animation setup for rotation
    val rotationAngle = remember { Animatable(0f) }
    LaunchedEffect(key1 = Unit) {
        rotationAngle.animateTo(
            targetValue = rotationAngle.value + 360f,
            animationSpec = tween(durationMillis = animDuration)
        )
    }

    if (rotateOnValueChange) {
        LaunchedEffect(pieData) {
            rotationAngle.snapTo(0f)
            rotationAngle.animateTo(
                targetValue = rotationAngle.value + 360f,
                animationSpec = tween(durationMillis = animDuration)
            )
        }
    }

    // Animate individual arcs' sweep angles
    val animatedSweepAngles = adjustedValues.map { value ->
        animateFloatAsState(
            targetValue = value,
            animationSpec = tween(durationMillis = animDuration)
        )
    }

    // Animate the chart's size change on each data update
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

    val density = LocalDensity.current
    val dpToPx = with(density) { chartBarWidth.toPx() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.animateContentSize()
    ) {
        DisplayLabels(
            labels = labels,
            colors = colors,
            customLabelStyle = customLabelStyle,
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
                animatedSweepAngles.forEachIndexed { index, animatedValue ->
                    drawArc(
                        color = colors[index],
                        startAngle = lastValue,
                        sweepAngle = animatedValue.value,
                        useCenter = false,
                        style = Stroke(dpToPx, cap = StrokeCap.Butt)
                    )

                    if (showLabelsInArcs) {
                        val midAngle = lastValue + animatedValue.value / 2
                        val angleRadians = Math.toRadians(midAngle.toDouble())

                        // Calculate the correct radius for label positioning
                        val labelRadius = size.minDimension / 1.45 - dpToPx / 1.45
                        val x = (size.center.x + labelRadius * cos(angleRadians)).toFloat()
                        val y = (size.center.y + labelRadius * sin(angleRadians)).toFloat()

                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                valueLabelFormatter(values[index]),
                                x,
                                y,
                                Paint().apply {
                                    color = textColor.toArgb()
                                    textAlign = Paint.Align.CENTER
                                    isFakeBoldText = true
                                    textSize = 15f * density.density
                                }
                            )
                        }
                    }

                    lastValue += animatedValue.value
                }
            }
        }

        Spacer(modifier = Modifier.height(44.dp))

        if (showValuesBelowChart) {
            var expanded by remember {
                mutableStateOf(
                    sharedPreferences.getBoolean(
                        "expanded_key",
                        false
                    )
                )
            }
            val scope = rememberCoroutineScope()
            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .width((100 * values.size).dp)
            ) {
                DisplayValues(
                    labels = labels,
                    values = values,
                    colors = colors,
                    customValueStyle = customValueStyle
                )
            }
            IconButton(
                onClick = {
                    expanded = !expanded
                    sharedPreferences.edit().putBoolean("expanded_key", expanded).apply()
                    scope.launch {
                        scrollState.scrollTo(if (expanded) 0 else scrollState.maxValue)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                Icon(
                    painter = rememberAnimatedVectorPainter(
                        AnimatedImageVector.animatedVectorResource(
                            R.drawable.anim_caret_down
                        ), !expanded
                    ),
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DisplayLabels(
    labels: List<String>,
    colors: List<Color>,
    customLabelStyle: @Composable (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Display each item in the pie chart
        labels.forEachIndexed { index, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(color = colors[index], shape = RoundedCornerShape(10.dp))
                        .size(16.dp)
                )
                customLabelStyle(label)
            }
        }
    }
}

@Composable
fun DisplayValues(
    labels: List<String>,
    values: List<Float>,
    colors: List<Color>,
    customValueStyle: @Composable ((String) -> Unit) = { DefaultValue(it) },
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .padding(16.dp)
    ) {
        // Display each item in the pie chart
        labels.forEachIndexed { index, label ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = colors[index], shape = RoundedCornerShape(10.dp))
                            .size(46.dp)
                    )
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.padding(start = 15.dp),
                            text = label,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        customValueStyle(values[index].toString())
                    }
                }
        }
    }
}

@Composable
private fun DefaultLabel(label: String) {
    Text(
        modifier = Modifier.padding(start = 12.dp),
        text = label,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun DefaultValue(value: String) {
    Text(
        modifier = Modifier.padding(start = 15.dp),
        text = value,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
@Preview
private fun DisplayValuesPreview() {
    val colors = listOf(Color.Red, Color.Blue, Color.Green)
    Box(
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp)
            .height(400.dp)
    ) {
        DisplayValues(
            colors = colors,
            labels = listOf("Concentrate", "Green Fodder", "Dry Roughage"),
            values = listOf(),
            customValueStyle = { DefaultValue(it) },
        )
    }
}