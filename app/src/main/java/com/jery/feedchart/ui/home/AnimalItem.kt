package com.jery.feedchart.ui.home

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jery.feedchart.R

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimalItem(name: String, description: String, iconRes: Int , onClick: () -> Unit) {
    ElevatedCard(
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(2f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(3f)) {
                Text(text = name, style = MaterialTheme.typography.headlineMedium)
                Text(text = description, style = MaterialTheme.typography.labelSmall)
            }
            Image(
                painter = rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.anim_caret_down), false),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .rotate(90f)
                    .scale(3f, 1.5f)
            )
        }
    }
}

@Preview
@Composable
fun AnimalItemPreview() {
    AnimalItem(
        name = "Cow",
        description = "A domestic animal",
        iconRes = R.drawable.ic_animal_cow,
        onClick = {}
    )
}