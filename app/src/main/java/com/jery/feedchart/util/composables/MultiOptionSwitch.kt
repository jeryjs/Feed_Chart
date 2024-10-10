package com.jery.feedchart.util.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MultiOptionSwitch(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedOption: Int,
    height: Dp = 64.dp,
    cornerRadius: Dp = 40.dp,
    selectedScale: Float = 1f,
    unselectedScale: Float = 0.7f,
    animationDuration: Int = 300,
    buttonsAlignment: Alignment.Vertical = Alignment.CenterVertically,
    buttonsArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    selectedTextColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTextColor: Color = MaterialTheme.colorScheme.secondary,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    onOptionSelected: (Int) -> Unit,
) {
    val density = LocalContext.current.resources.displayMetrics.density
    var widgetWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .onSizeChanged { size ->
                widgetWidth = size.width // Capture the width of the widget
            }
    ) {
        val selectedFontSize = widgetWidth * 0.018f
        val unselectedFontSize = widgetWidth * 0.018f
        val fontScalingFactor by animateFloatAsState(selectedFontSize, tween(animationDuration))

        val transition = updateTransition(selectedOption, label = "slimeTransition")
        val backgroundOffset by transition.animateFloat(transitionSpec = { tween(animationDuration, easing = FastOutSlowInEasing) }, label = "backgroundOffset") { ((widgetWidth / options.size) * it).toFloat() }
        val backgroundWidth by transition.animateFloat(transitionSpec = { tween(animationDuration, easing = FastOutSlowInEasing) }, label = "backgroundWidth") { (widgetWidth / options.size + it).toFloat() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = backgroundColor,
                topLeft = Offset(backgroundOffset, 0f),
                size = Size(backgroundWidth, size.height),
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
            )
        }

        Row(
            horizontalArrangement = buttonsArrangement,
            verticalAlignment = buttonsAlignment,
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { index, option ->
                val scale by animateFloatAsState(
                    targetValue = if (selectedOption == index) selectedScale else unselectedScale,
                    animationSpec = tween(animationDuration)
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale)
                        .clip(RoundedCornerShape(50))
                        .clickable(
                            interactionSource = null,
                            indication = null,
                        ) { onOptionSelected(index) }
                        .padding(vertical = 12.dp)
                        .background(Color.Transparent)
                ) {
                    Text(
                        text = option,
                        fontSize = with(density) { if (selectedOption == index) fontScalingFactor.sp else unselectedFontSize.sp },
                        textAlign = TextAlign.Center,
                        style = textStyle,
                        color = if (selectedOption == index) selectedTextColor else unselectedTextColor,
                    )
                }
            }
        }
    }
}
