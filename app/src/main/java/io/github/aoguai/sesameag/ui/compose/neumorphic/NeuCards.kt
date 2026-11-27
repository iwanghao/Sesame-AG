package io.github.aoguai.sesameag.ui.compose.neumorphic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import io.github.aoguai.sesameag.ui.theme.NeuElevation
import io.github.aoguai.sesameag.ui.theme.NeumorphicShape
import io.github.aoguai.sesameag.ui.theme.neumorphicColors
import io.github.aoguai.sesameag.ui.theme.neumorphicRaised
import io.github.aoguai.sesameag.ui.theme.neumorphicSunken

/**
 * 新拟态卡片（与 M3 Card 同名同参数）。
 *
 * 卡片本体统一为「与背景同色的凸起块 + 大圆角」；
 * 调用方传入的 containerColor 若带有语义（例如选中态的 primaryContainer），
 * 会被保留并以低透明度叠在底色上，从而既保持新拟态质感，又不丢失原有语义。
 */
@Composable
private fun resolveCardFill(containerColor: Color): Color {
    val neu = neumorphicColors
    val scheme = MaterialTheme.colorScheme
    val isSameAsBackground = containerColor == Color.Unspecified ||
        containerColor == scheme.surface ||
        containerColor == scheme.surfaceContainerLowest ||
        containerColor == scheme.surfaceContainerLow ||
        containerColor == scheme.surfaceContainer ||
        containerColor == scheme.surfaceBright
    // 主题层已把语义容器色（primary/error/tertiary/secondaryContainer）收敛为低饱和淡染，
    // 这里直接原样使用，避免再叠一次 alpha 导致颜色被冲淡甚至偏白。
    val isTintedContainer = containerColor == scheme.primaryContainer ||
        containerColor == scheme.errorContainer ||
        containerColor == scheme.tertiaryContainer ||
        containerColor == scheme.secondaryContainer ||
        containerColor == scheme.onPrimaryContainer ||
        containerColor == scheme.onErrorContainer
    return when {
        isSameAsBackground -> neu.background
        // 高一层容器在主题里对应凹陷底，用作卡片时取底板色，靠阴影区分层级
        containerColor == scheme.surfaceContainerHigh ||
            containerColor == scheme.surfaceContainerHighest -> neu.background
        isTintedContainer -> containerColor
        containerColor.alpha < 1f -> containerColor
        // 其他语义色：淡染到底板，保留语义但不破坏同色质感
        else -> containerColor.copy(alpha = 0.22f).compositeOver(neu.background)
    }
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    shape: Shape = NeumorphicShape.l,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val fill = resolveCardFill(colors.containerColor)
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.l
    Box(
        modifier = modifier.neumorphicRaised(
            shape = rounded,
            colors = neumorphicColors,
            spec = NeuElevation.raised,
            fill = fill,
        ),
    ) {
        androidx.compose.foundation.layout.Column(content = content)
    }
}

@Composable
fun Card(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.l,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val fill = resolveCardFill(colors.containerColor)
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.l
    NeuCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = rounded,
        selected = fill != neumorphicColors.background,
        selectedColor = colors.containerColor,
        content = { androidx.compose.foundation.layout.Column(content = content) },
    )
}

/** 与 M3 ElevatedCard 同名：新拟态下统一表现为更强的凸起 */
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = NeumorphicShape.l,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.elevatedCardElevation(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val fill = resolveCardFill(colors.containerColor)
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.l
    Box(
        modifier = modifier.neumorphicRaised(
            shape = rounded,
            colors = neumorphicColors,
            spec = NeuElevation.high,
            fill = fill,
        ),
    ) {
        androidx.compose.foundation.layout.Column(content = content)
    }
}

/** 与 M3 OutlinedCard 同名：新拟态下用凹陷表现「描边容器」 */
@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    shape: Shape = NeumorphicShape.l,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.l
    Box(
        modifier = modifier.neumorphicSunken(shape = rounded, colors = neumorphicColors),
    ) {
        androidx.compose.foundation.layout.Column(content = content)
    }
}

@Composable
fun OutlinedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.l,
    colors: CardColors = CardDefaults.outlinedCardColors(),
    elevation: CardElevation = CardDefaults.outlinedCardElevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit,
) {
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.l
    NeuCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = rounded,
        content = { androidx.compose.foundation.layout.Column(content = content) },
    )
}
