package com.cs407.saleselector.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont

import androidx.compose.ui.unit.sp
import com.cs407.saleselector.R

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)


// 👇 Renamed this to be more descriptive
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val pacificoFont = GoogleFont("Yellowtail")
val titilliumFont = GoogleFont("Titillium Web")


// 👇 Renamed this as well
@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
val pacificoFontFamily = FontFamily(
    Font(googleFont = pacificoFont, fontProvider = provider, weight = FontWeight.Normal)
)
val titilliumFontFamily = FontFamily(
    Font(googleFont = titilliumFont, fontProvider = provider, weight = FontWeight.Normal)
)

// Set up the Typography to use the new font family
val Typography = Typography(
    // Apply the font to the styles you want to change.
    // For a signature font, it's best suited for large, decorative text like displayLarge.
    displayLarge = TextStyle(
        fontFamily = pacificoFontFamily, // <-- Use the new font family
        fontWeight = FontWeight.Bold, // Momo Signature is best at a normal weight
        fontSize = 50.sp
    ),
    // You can apply it to other styles too
    displayMedium = TextStyle(
        fontFamily = titilliumFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp
    ),
    // Keep other text styles (like body and labels) with a more readable font.
    // Using the default font here.
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // <-- Use a readable font for body text
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = titilliumFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = titilliumFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = titilliumFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = titilliumFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
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
