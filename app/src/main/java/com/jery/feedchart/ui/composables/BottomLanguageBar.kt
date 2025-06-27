package com.jery.feedchart.ui.composables

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.jery.feedchart.util.LocaleUtils
import java.util.Locale

@Composable
fun BottomLanguageBar() {
    val context = LocalContext.current
    val localeUtils = LocaleUtils(context)
    val languages = localeUtils.availableLanguages()
    val selectedLangCode = localeUtils.getPersistedLocale() ?: Locale.getDefault().language

    LazyRow(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
    ) {
        items(languages) { language ->
            val (langName, langCode) = language.split(":")
            val isSelected = langCode == selectedLangCode
            TextButton(
                onClick = {
                    localeUtils.setLocale(langCode)
                    ActivityCompat.recreate(context as Activity)
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(24.dp)
                    .width(IntrinsicSize.Min)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Transparent,
                        MaterialTheme.shapes.small
                    )
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = langName,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Top),
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )
            }
            if(langName != languages.last().split(":").first()) Text(text = "|")
        }
    }
}

@Preview
@Composable
fun PreviewBottomLanguageBar() {
    BottomLanguageBar()
}