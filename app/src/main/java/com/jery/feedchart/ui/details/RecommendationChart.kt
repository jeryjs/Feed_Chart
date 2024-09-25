package com.jery.feedchart.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jery.feedchart.data.model.FeedDetails

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
