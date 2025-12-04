package com.cs407.saleselector.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.cs407.saleselector.R // Make sure this matches your package name

// 1. Initialize the Google Font provider
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// 2. Define the font to download
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val ralewayFont = GoogleFont("Raleway")

// 3. Create the FontFamily
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val ralewayFontFamily = FontFamily(
    Font(googleFont = ralewayFont, fontProvider = provider),
    Font(googleFont = ralewayFont, fontProvider = provider, weight = FontWeight.Bold)
)

// 4. Set up the Typography to use the new font family
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = ralewayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = ralewayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    // Define for other styles like titleLarge, bodyLarge, labelSmall, etc.
    // Example for body text and buttons (label)
    bodyLarge = TextStyle(
        fontFamily = ralewayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = ralewayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
