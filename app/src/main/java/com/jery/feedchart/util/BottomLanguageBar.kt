package com.jery.feedchart.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BottomLanguageBar() {
    val context = LocalContext.current
    val localeUtils = LocaleUtils(context)
    val languages = localeUtils.availableLanguages()

    LazyRow(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(languages) { language ->
            val (langName, langCode) = language.split(":")
//            AssistChip(
//                onClick = { localeUtils.setLocale(langCode) },
//                label = { Text(text = langName) },
//                colors = AssistChipDefaults.assistChipColors()
//            )
            TextButton(
                onClick = { localeUtils.setLocale(langCode) }
            ) {
                Text(text = langName)
            }
            Text(text = "|")
        }
    }
}

@Preview
@Composable
fun PreviewBottomLanguageBar() {
    BottomLanguageBar()
}