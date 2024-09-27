package com.jery.feedchart.util.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jery.feedchart.util.LocaleUtils

@Composable
fun BottomLanguageBar() {
    val context = LocalContext.current
    val localeUtils = LocaleUtils(context)
    val languages = localeUtils.availableLanguages()

    LazyRow(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(35.dp)
    ) {
        items(languages) { language ->
            val (langName, langCode) = language.split(":")
            TextButton(
                onClick = { localeUtils.setLocale(langCode) }
            ) {
                Text(text = langName)
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