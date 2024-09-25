package com.jery.feedchart.ui.details

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jery.feedchart.R
import com.jery.feedchart.data.model.FeedDetails
import com.jery.feedchart.data.model.FeedRecommendation
import com.jery.feedchart.data.model.FodderAvailability
import com.jery.feedchart.data.repository.FeedRepository
import com.jery.feedchart.ui.details.ActionChips
import com.jery.feedchart.ui.theme.FeedChartTheme
import com.jery.feedchart.util.BottomLanguageBar
import java.util.Locale

class DetailsActivity : ComponentActivity() {

    private lateinit var feedRecommendations: Map<Int, List<FeedRecommendation>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedRecommendations = FeedRepository(this).getRecommendations()

        val animalId = intent.getIntExtra("ANIMAL_ID", -1)

        setContent {
            FeedChartTheme {
                DetailsScreen(animalId, feedRecommendations)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(animalId: Int, feedRecommendations: Map<Int, List<FeedRecommendation>>) {
    var selectedMilkYield by remember { mutableStateOf<Float?>(5f) }
    var selectedFodderAvailability by remember { mutableStateOf(FodderAvailability.HIGH) }
    val recommendations = feedRecommendations[animalId] ?: emptyList()

    Scaffold(
        topBar = { MyAppBar() },
        bottomBar = { BottomLanguageBar() },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            MilkYieldSelector(
                recommendations = recommendations,
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
                animalId = animalId,
                selectedMilkYield = selectedMilkYield,
                selectedFodderAvailability = selectedFodderAvailability,
                feedRecommendations = feedRecommendations
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(animalId: Int = 0) {
    val activity = LocalContext.current as Activity
    TopAppBar(
        title = { Text(text = stringArrayResource(R.array.animal_desc)[animalId]) },
        navigationIcon = {
            IconButton(onClick = { activity.finish() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Go Back"
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.MoreVert),
                    contentDescription = "More Actions",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ActionChips()
            }
        }
    )
}

@Composable
fun MilkYieldSelector(
    recommendations: List<FeedRecommendation>,
    selectedMilkYield: Float?,
    onMilkYieldSelected: (Float?) -> Unit,
) {
    val milkYieldValues = recommendations.map { it.milkYield }
    CustomStepSlider(
        values = milkYieldValues as List<Any>,
        selectedValue = selectedMilkYield ?: milkYieldValues.first(),
        onValueSelected = { newValue -> onMilkYieldSelected(newValue as? Float) },
        label = "Milk Yield (lit/day)"
    )
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
fun FeedRecommendationDisplay(
    animalId: Int,
    selectedMilkYield: Float?,
    selectedFodderAvailability: FodderAvailability,
    feedRecommendations: Map<Int, List<FeedRecommendation>>,
) {
    Text(
        text = "Feed Recommendation (per day)",
        color = Color(0xFFB22222), // Red color
        fontWeight = FontWeight.Bold
    )

    val recommendation = getRecommendation(
        animalId,
        selectedMilkYield,
        selectedFodderAvailability,
        feedRecommendations
    )
    RecommendationChart(recommendation)
}

@Composable
fun ActionChips() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionChip(text = stringResource(R.string.details))
        ActionChip(text = stringResource(R.string.request_form))
        ActionChip(text = stringResource(R.string.feedback))
    }
}

@Composable
fun ActionChip(text: String) {
    AssistChip(
        onClick = { /*TODO*/ },
        label = {
            Text(
                text = text.uppercase(Locale.getDefault()),
                fontSize = 14.sp
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFF3F51B5),
            labelColor = Color.White
        ),
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
fun DropdownMenu(items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Text(
            text = selectedItem,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    text = { Text(text = item) }
                )
            }
        }
    }
}

@Composable
fun RadioGroup(selectedOption: FodderAvailability, onOptionSelected: (FodderAvailability) -> Unit) {
    Column {
        FodderAvailability.values().forEach { option ->
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

fun getRecommendation(
    animalId: Int,
    milkYield: Float?,
    fodderAvailability: FodderAvailability,
    feedRecommendations: Map<Int, List<FeedRecommendation>>,
): FeedDetails? {
    val recommendations = feedRecommendations[animalId] ?: emptyList()
    return recommendations.find { it.milkYield == milkYield }?.greenFodderAvailability?.get(
        fodderAvailability
    )
}
