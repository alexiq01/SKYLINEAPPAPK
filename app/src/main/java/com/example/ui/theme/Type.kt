package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// --- Apple Font Families Definition ---

/**
 * Apple SF Pro FontFamily (Display & Text)
 */
val SFProFontFamily = FontFamily(
    Font(R.font.sf_pro_regular, FontWeight.Normal),
    Font(R.font.sf_pro_medium, FontWeight.Medium),
    Font(R.font.sf_pro_bold, FontWeight.Bold)
)

/**
 * Apple SF Compact FontFamily (Optimized for dense and compact displays/smart indicators)
 */
val SFCompactFontFamily = FontFamily(
    Font(R.font.sf_compact_regular, FontWeight.Normal),
    Font(R.font.sf_compact_bold, FontWeight.Bold)
)

/**
 * Apple SF Mono FontFamily (Monospaced, perfect for numbers, IPs, codes, and tokens)
 */
val SFMonoFontFamily = FontFamily(
    Font(R.font.sf_mono_regular, FontWeight.Normal),
    Font(R.font.sf_mono_bold, FontWeight.Bold)
)

/**
 * Apple New York FontFamily (Apple's serif typeface for elegant headings & editorial styling)
 */
val NewYorkFontFamily = FontFamily(
    Font(R.font.new_york_regular, FontWeight.Normal),
    Font(R.font.new_york_bold, FontWeight.Bold)
)

/**
 * Apple SF Arabic FontFamily (Native Arabic typography designed with SF proportions)
 */
val SFArabicFontFamily = FontFamily(
    Font(R.font.sf_arabic_regular, FontWeight.Normal)
)

// Primary default font for the application: SF Pro paired with Apple typographic hierarchy
val AppFontFamily = SFProFontFamily

// --- Material 3 Typography using SF Pro & New York & SF Mono ---
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = NewYorkFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 54.sp,
        lineHeight = 62.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = NewYorkFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.2).sp
    ),
    displaySmall = TextStyle(
        fontFamily = NewYorkFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 42.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SFProFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SFCompactFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SFCompactFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SFCompactFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
