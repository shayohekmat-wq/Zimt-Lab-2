package de.zimt.eimerliste.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Ink {
    val Night = Color(0xFF221811)
    val Walnut = Color(0xFF3E281E)
    val WalnutLight = Color(0xFF61422B)
    val Paper = Color(0xFFF2E4C9)
    val PaperEdge = Color(0xFFD4B98E)
    val Brass = Color(0xFFE1B76C)
    val BrassDark = Color(0xFF846139)
    val Charcoal = Color(0xFF30241D)
    val Muted = Color(0xFFCDBB9E)
    val InkBlack = Color(0xFF3B2C24)
}

@Composable
fun EimerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Ink.Brass,
            onPrimary = Ink.Night,
            background = Ink.Night,
            onBackground = Ink.Paper,
            surface = Ink.Walnut,
            onSurface = Ink.Paper
        ),
        content = content
    )
}
