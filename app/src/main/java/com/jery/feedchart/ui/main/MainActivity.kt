package com.jery.feedchart.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.jery.feedchart.R
import com.jery.feedchart.ui.details.DetailsActivity
import com.jery.feedchart.ui.theme.FeedChartTheme
import com.jery.feedchart.util.BottomLanguageBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedChartTheme {
                MainScreen { animalId ->
                    val intent = Intent(this, DetailsActivity::class.java)
                    intent.putExtra("ANIMAL_ID", animalId)
                    startActivity(intent)
                }
            }
        }
    }
}

class OnAnimalClickProvider : PreviewParameterProvider<(Int) -> Unit> {
    override val values: Sequence<(Int) -> Unit> = sequenceOf({})
}

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreen(@PreviewParameter(OnAnimalClickProvider::class) onAnimalClick: (Int) -> Unit) {
    val animalTypes = stringArrayResource(id = R.array.animal_types)
    val animalIcons = remember { listOf(R.drawable.ic_animal_cow, R.drawable.ic_animal_buffalo) }
    val animalDesc = stringArrayResource(id = R.array.animal_desc)

    Scaffold(
        topBar = { MyAppBar() },
        bottomBar = { BottomLanguageBar() }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxHeight()
        ) {
            var expanded by remember { mutableStateOf(false) }
            var dragOffset by remember { mutableStateOf(0f) }

            ElevatedCard(
                colors = CardDefaults.cardColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier
                    .padding(4.dp)
                    .padding(bottom = 32.dp)
                    .clickable(onClick = { expanded = !expanded })
                    .draggable(
                        state = rememberDraggableState { delta ->
                            dragOffset += delta
                            if (dragOffset > 100) {
                                expanded = true
                            } else if (dragOffset < -100) {
                                expanded = false
                            }
                        },
                        orientation = Orientation.Vertical
                    )
                    .animateContentSize(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_title),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    if (expanded) {
                        Text(
                            text = stringResource(R.string.app_description),
                            textAlign = TextAlign.Justify,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.read_more),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.End)
                        )
                    }
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 2.dp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(animalTypes.indices.toList()) { index ->
                    AnimalItem(
                        name = animalTypes[index],
                        description = animalDesc[index],
                        iconRes = animalIcons[index],
                        onClick = { onAnimalClick(index) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//@Preview
fun MyAppBar() {
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Image(
                painter = painterResource(id = R.drawable.icar_nianp_header),
                contentDescription = null,
                alignment = AbsoluteAlignment.CenterLeft,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    )
}
