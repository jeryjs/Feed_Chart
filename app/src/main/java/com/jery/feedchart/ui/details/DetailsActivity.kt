package com.jery.feedchart.ui.details

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.jery.feedchart.R
import com.jery.feedchart.data.repository.FeedRepository
import com.jery.feedchart.ui.theme.FeedChartTheme
import com.jery.feedchart.util.composables.BottomLanguageBar
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class DetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val animalId = intent.getIntExtra("ANIMAL_ID", -1)
        val feedRecommendations = FeedRepository(this).getRecommendations()[animalId] ?: emptyList()

        setContent {
            FeedChartTheme {
                Scaffold(
                    topBar = { MyAppBar(animalId) },
                    bottomBar = { BottomLanguageBar() },
                ) { paddingValues ->
                    Box(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        when (feedRecommendations.first().displayType) {
                            0 -> MilkYieldScreen(feedRecommendations)
                            1 -> BodyWeightScreen(feedRecommendations)
                            else -> null
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun MyAppBar(animalId: Int = 0) {
    val activity = LocalContext.current as Activity
    var showExtendedMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.animateContentSize()) {
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
                containerColor = if(isSystemInDarkTheme()) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = {
                IconButton(onClick = { showExtendedMenu = !showExtendedMenu }) {
                    Image(
                        painter = rememberAnimatedVectorPainter(AnimatedImageVector.animatedVectorResource(R.drawable.anim_more_enter), showExtendedMenu),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = Modifier.rotate(90f)
                    )
                }
            }
        )
        if (showExtendedMenu)
            ActionChips()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionChips() {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var sheetContent by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionChip(text = "Details", onClick = {
            sheetContent = "details"
            openBottomSheet = true
        })
        ActionChip(text = "Request Form", onClick = {
            sheetContent = "request_form"
            openBottomSheet = true
        })
        ActionChip(text = "Feedback", onClick = {
            val feedbackIntent = Intent(
                Intent.ACTION_VIEW,
//                Uri.parse("market://details?id=" + context.packageName)
                Uri.parse("market://details?id=" + "com.borne.root.nianp_feedchart")
            )
            context.startActivity(feedbackIntent)
        })
    }

    // Modal Bottom Sheet content
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            when (sheetContent) {
                "details" -> {
                    ReckonerDetailsContent()
                }

                "request_form" -> {
                    RequestFormContent(
                        onDownload = {
                            val requestForm = downloadRequestFormAsPdf(context as Activity)
                            requestForm?.let {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(FileProvider.getUriForFile(context, "${context.packageName}.provider", it), "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            }
                        },
                        onSendMail = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_SUBJECT, "Request Form")
                                putExtra(Intent.EXTRA_TEXT, "Please find the request form attached.")
                                val inputStream = context.assets.open("request_form.pdf")
                                val file = File(context.cacheDir, "request_form.pdf")
                                inputStream.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(Intent.EXTRA_EMAIL, arrayOf("rajnutri@gmail.com"))
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Send email..."))
                        }
                    )
                }
            }
        }

        // Handle Back navigation to close the BottomSheet
        BackHandler {
            scope.launch {
                bottomSheetState.hide()
            }.invokeOnCompletion {
                openBottomSheet = false
            }
        }
    }
}

@Composable
fun ActionChip(text: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = text.uppercase(Locale.getDefault()),
                fontSize = 14.sp
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primary,
            labelColor = Color.White
        ),
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
fun ReckonerDetailsContent() {
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Cattle Details", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Text(
                "Here are the details for different cattle types:",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {
            Text("Dairy Cows", style = MaterialTheme.typography.titleMedium)
            Text("Milk Yield: 15-25 liters per day", style = MaterialTheme.typography.bodySmall)
            Text(
                "Feed Type: High protein, moderate carbohydrates",
                style = MaterialTheme.typography.bodySmall
            )
        }
        item {
            Text("Buffaloes", style = MaterialTheme.typography.titleMedium)
            Text("Milk Yield: 10-20 liters per day", style = MaterialTheme.typography.bodySmall)
            Text(
                "Feed Type: High fiber, moderate protein",
                style = MaterialTheme.typography.bodySmall
            )
        }
        // Add more detailed items as required.
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestFormContent(onDownload: () -> Unit, onSendMail: () -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Request Form", style = MaterialTheme.typography.titleLarge)
            Row {
                IconButton(onClick = onSendMail, colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                }
                IconButton(onClick = onDownload, colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    @Suppress("DEPRECATION")
                    Icon(Icons.Outlined.ArrowForward, contentDescription = null, modifier = Modifier.rotate(90f))
                }
            }
        }
        LocalContext.current.resources.openRawResource(R.raw.request_form).use { image ->
            val bitmap = BitmapFactory.decodeStream(image)
            Card {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Request Form",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


private fun downloadRequestFormAsPdf(context: Activity): File? {
    try {
        val inputStream = context.assets.open("request_form.pdf")
        val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val outputFile = File(downloadsDir, "request_form.pdf")

        inputStream.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        Toast.makeText(context, "${outputFile.name} saved to Downloads", Toast.LENGTH_LONG).show()
        return outputFile
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to download request form: ${e.message}", Toast.LENGTH_LONG).show()
        return null
    }
}