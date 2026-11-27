package io.github.aoguai.sesameag.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val appShapes = Shapes(
    extraSmall = NeumorphicShape.xs,
    small = NeumorphicShape.s,
    medium = NeumorphicShape.m,
    large = NeumorphicShape.l,
    extraLarge = NeumorphicShape.xl,
    largeIncreased = NeumorphicShape.l,
    extraExtraLarge = NeumorphicShape.xl,
)

/**
 * 把动态取色 / 明暗主题统一收敛到新拟态底色。
 *
 * 新拟态的前提是「所有元素与背景同色」，所以 surface 系列全部对齐到同一块底板，
 * 只保留 primary / error 等语义色用于强调，避免 M3 默认的多层灰阶破坏整体质感。
 */
private fun ColorScheme.toNeumorphicScheme(): ColorScheme {
    val neu = if (this.background.luminance() > 0.5f) {
        NeumorphicColors(
            background = NeumorphicBackgroundLight,
            shadow = NeumorphicShadowLight,
            light = NeumorphicLightLight,
            accent = primary,
            onAccent = onPrimary,
            content = onSurface,
            contentMuted = onSurfaceVariant,
            error = error,
            sunken = Color(0xFFE3E7EA),
            raised = NeumorphicBackgroundLight,
            field = Color(0xFFE4E8EB),
            track = Color(0xFFE6EAED),
            hairline = Color(0xFFCBD3D9),
        )
    } else {
        NeumorphicColors(
            background = NeumorphicBackgroundDark,
            shadow = NeumorphicShadowDark,
            light = NeumorphicLightDark,
            accent = primary,
            onAccent = onPrimary,
            content = onSurface,
            contentMuted = onSurfaceVariant,
            error = error,
            sunken = Color(0xFF191D21),
            raised = NeumorphicBackgroundDark,
            field = Color(0xFF2C3339),
            track = Color(0xFF2E353C),
            hairline = Color(0xFF414B54),
        )
    }
    return copy(
        background = neu.background,
        onBackground = neu.content,
        surface = neu.background,
        onSurface = neu.content,
        surfaceVariant = neu.sunken,
        onSurfaceVariant = neu.contentMuted,
        surfaceContainerLowest = neu.background,
        surfaceContainerLow = neu.background,
        surfaceContainer = neu.background,
        surfaceContainerHigh = neu.sunken,
        surfaceContainerHighest = neu.sunken,
        surfaceDim = neu.sunken,
        surfaceBright = neu.background,
        outline = neu.contentMuted,
        outlineVariant = neu.shadow.copy(alpha = 0.45f),
        primary = neu.accent,
        onPrimary = neu.onAccent,
        error = neu.error,
        // 语义容器色全部收敛为「低饱和淡染」：单靠 primary/tertiary/error 淡淡叠在底板上，
        // 既保留「这块是提示/错误」的语义，又不会出现整块高饱和色块破坏新拟态的统一材质。
        primaryContainer = neu.accent.copy(alpha = 0.16f).compositeOver(neu.background),
        onPrimaryContainer = neu.content,
        secondaryContainer = neu.sunken,
        onSecondaryContainer = neu.content,
        tertiaryContainer = neu.accent.copy(alpha = 0.10f).compositeOver(neu.background),
        onTertiaryContainer = neu.content,
        errorContainer = neu.error.copy(alpha = 0.13f).compositeOver(neu.background),
        onErrorContainer = neu.error.copy(alpha = 0.92f).compositeOver(neu.background),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val rawScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }
    val colorScheme = rawScheme.toNeumorphicScheme()

    ProvideNeumorphicColors(darkTheme = darkTheme) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = appShapes,
            content = content
        )
    }
}
