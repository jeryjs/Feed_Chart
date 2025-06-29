package com.jery.feedchart.ui.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.jery.feedchart.R
import com.jery.feedchart.data.repository.FeedRepository
import com.jery.feedchart.ui.composables.BottomLanguageBar
import com.jery.feedchart.ui.theme.FeedChartTheme
import com.jery.feedchart.util.LocaleUtils
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class DetailsActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.wrapContext(newBase))
    }

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
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                            .fillMaxHeight()
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
    val activity = LocalActivity.current
    var showExtendedMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.animateContentSize()) {
        TopAppBar(
            title = { Text(text = stringArrayResource(R.array.animal_desc)[animalId]) },
            navigationIcon = {
                IconButton(onClick = { activity?.finish() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Go Back"
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary,
                titleContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                IconButton(onClick = { showExtendedMenu = !showExtendedMenu }) {
                    Image(
                        painter = rememberAnimatedVectorPainter(
                            AnimatedImageVector.animatedVectorResource(
                                R.drawable.anim_more_enter
                            ), showExtendedMenu
                        ),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = Modifier.rotate(90f)
                    )
                }
            }
        )
        if (showExtendedMenu)
            ActionChips(animalId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionChips(animalId: Int) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var sheetContent by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionChip(text = stringResource(R.string.details), onClick = {
            sheetContent = "details"
            openBottomSheet = true
        })
        ActionChip(text = stringResource(R.string.request_form), onClick = {
            sheetContent = "request_form"
            openBottomSheet = true
        })
        ActionChip(text = stringResource(R.string.feedback), onClick = {
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
                    ReckonerDetailsContent(animalId = animalId)
                }

                "request_form" -> {
                    RequestFormContent(
                        onDownload = {
                            val requestForm = downloadRequestFormAsPdf(context as Activity)
                            requestForm?.let {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(
                                        FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            it
                                        ), "application/pdf"
                                    )
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            }
                        },
                        onSendMail = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_SUBJECT, "Request Form")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Please find the request form attached."
                                )
                                val inputStream = context.assets.open("request_form.pdf")
                                val file = File(context.cacheDir, "request_form.pdf")
                                inputStream.use { input ->
                                    file.outputStream().use { output -> input.copyTo(output) }
                                }
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    file
                                )
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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        modifier = Modifier.padding(4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReckonerDetailsContent(animalId: Int) {
    val animalDetails =
        stringArrayResource(R.array.animal_details).getOrNull(animalId) ?: "Details not found"
    val richTextState = rememberRichTextState()

    richTextState.setMarkdown(animalDetails)

    RichTextEditor(
        state = richTextState,
        readOnly = true,
        colors = RichTextEditorDefaults.richTextEditorColors(
            containerColor = Color.Transparent
        )
    )
}

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
                IconButton(
                    onClick = onSendMail,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                }
                IconButton(
                    onClick = onDownload,
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    @Suppress("DEPRECATION")
                    Icon(
                        Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.rotate(90f)
                    )
                }
            }
        }
        LocalContext.current.resources.openRawResource(R.raw.request_form).use { image ->
            val bitmap = BitmapFactory.decodeStream(image)
            Card(
                // Set background to White to avoid the pdf not being visible on dark mode
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
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
        Toast.makeText(context, "Failed to download request form: ${e.message}", Toast.LENGTH_LONG)
            .show()
        return null
    }
}