package io.github.aoguai.sesameag.ui.compose.neumorphic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.aoguai.sesameag.ui.theme.NeuElevation
import io.github.aoguai.sesameag.ui.theme.NeumorphicShape
import io.github.aoguai.sesameag.ui.theme.neumorphicColors
import io.github.aoguai.sesameag.ui.theme.neumorphicRaised
import io.github.aoguai.sesameag.ui.theme.neumorphicSunken

/**
 * 新拟态 Surface（与 M3 Surface 同名）。
 *
 * M3 的 Surface 在页面里承担「卡片 / 分组容器 / 强调块」等不同职责，
 * 新拟态下统一策略：
 * - 普通容器 -> 同色凸起
 * - 传入语义容器色（primaryContainer / errorContainer / tertiaryContainer）-> 语义淡染 + 凸起
 * - 传入 onClick -> 可点击卡片，按下凹陷
 */
@Composable
private fun resolveSurfaceFill(color: Color): Color {
    val neu = neumorphicColors
    val scheme = MaterialTheme.colorScheme
    // 主题已把 surface 系列全部映射到底板色，这里直接视为「同底板」，不再二次淡染
    val isSameAsBackground = color == Color.Unspecified ||
        color == scheme.surface ||
        color == scheme.surfaceContainerLowest ||
        color == scheme.surfaceContainerLow ||
        color == scheme.surfaceContainer ||
        color == scheme.surfaceBright
    // 语义容器色已在主题层淡染好，直接原样使用，避免双重冲淡
    val isTintedContainer = color == scheme.primaryContainer ||
        color == scheme.errorContainer ||
        color == scheme.tertiaryContainer ||
        color == scheme.secondaryContainer ||
        color == scheme.onPrimaryContainer ||
        color == scheme.onErrorContainer
    return when {
        isSameAsBackground -> neu.background
        color == scheme.surfaceContainerHigh ||
            color == scheme.surfaceContainerHighest ||
            color == scheme.surfaceDim -> neu.sunken
        isTintedContainer -> color
        color.alpha < 1f -> color
        else -> color.copy(alpha = 0.22f).compositeOver(neu.background)
    }
}

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: Shape = NeumorphicShape.l,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorForSafe(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    /** 自定义阴影档位：弹窗等场景传入 [NeuElevation.dialog]，避免默认 raised 的高光过亮 */
    elevationSpec: io.github.aoguai.sesameag.ui.theme.NeumorphicSpec? = null,
    content: @Composable () -> Unit,
) {
    val neu = neumorphicColors
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.l
    val fill = resolveSurfaceFill(color)
    val spec = elevationSpec ?: if (tonalElevation > 0.dp || shadowElevation > 0.dp) NeuElevation.high else NeuElevation.raised
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.material3.LocalContentColor provides contentColor,
        androidx.compose.material3.LocalTextStyle provides MaterialTheme.typography.bodyMedium,
    ) {
        Box(
            modifier = modifier.neumorphicRaised(
                shape = rounded,
                colors = neu,
                spec = spec,
                fill = fill,
            ),
        ) {
            content()
        }
    }
}

@Composable
fun Surface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.l,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorForSafe(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.l
    val fill = resolveSurfaceFill(color)
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.material3.LocalContentColor provides contentColor,
        androidx.compose.material3.LocalTextStyle provides MaterialTheme.typography.bodyMedium,
    ) {
        NeuCard(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            shape = rounded,
            selected = fill != neumorphicColors.background,
            selectedColor = color,
        ) { content() }
    }
}

/** 语义容器色的前景色（沿用 M3 约定，避免文字对比度下降） */
@Composable
private fun contentColorForSafe(color: Color): Color {
    val scheme = MaterialTheme.colorScheme
    return when (color) {
        scheme.primaryContainer -> scheme.onPrimaryContainer
        scheme.secondaryContainer -> scheme.onSecondaryContainer
        scheme.tertiaryContainer -> scheme.onTertiaryContainer
        scheme.errorContainer -> scheme.onErrorContainer
        scheme.surfaceVariant -> scheme.onSurfaceVariant
        else -> scheme.onSurface
    }
}

/**
 * 浅凹容器：用于搜索栏、分段控件底座、卡片内嵌信息块等。
 *
 * 与输入框共用「浅内底 + 细描边」的规则：只比底板暗一档，
 * 保证是「轻轻下沉」而不是「挖出一块深色区域」。
 */
@Composable
fun SunkenSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = NeumorphicShape.m,
    content: @Composable ColumnScope.() -> Unit,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .clip(shape)
            .background(neumorphicColors.field)
            .border(1.dp, neumorphicColors.hairline, shape),
        content = content,
    )
}

// ---------------------------------------------------------------------------

/**
 * 新拟态对话框（与 M3 AlertDialog 同名）。
 *
 * 对话框本身是页面之上的一层「浮板」，因此用最高档的凸起 + 大圆角，
 * 并与页面底板同色，保证弹窗和页面属于同一材质。
 */
@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    shape: Shape = NeumorphicShape.xl,
    containerColor: Color = neumorphicColors.background,
    iconContentColor: Color = neumorphicColors.accent,
    titleContentColor: Color = neumorphicColors.content,
    textContentColor: Color = neumorphicColors.contentMuted,
) {
    val neu = neumorphicColors
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.xl
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest,
        properties = NeuDialogProperties,
    ) {
        Box(
            modifier = modifier
                .neumorphicDialogPlate()
                .neumorphicRaised(
                    shape = rounded,
                    colors = neu,
                    // 弹窗浮板用柔和档位：投影比卡片略深即可，高光压低避免白边
                    spec = NeuElevation.dialog,
                    fill = if (containerColor == neu.background) neu.background else containerColor.copy(alpha = 0.22f).compositeOver(neu.background),
                ),
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(24.dp),
            ) {
                icon?.let {
                    Box(Modifier.padding(bottom = 16.dp)) {
                        androidx.compose.runtime.CompositionLocalProvider(
                            androidx.compose.material3.LocalContentColor provides iconContentColor,
                        ) { it() }
                    }
                }
                title?.let {
                    Box(Modifier.padding(bottom = 12.dp)) {
                        androidx.compose.runtime.CompositionLocalProvider(
                            androidx.compose.material3.LocalContentColor provides titleContentColor,
                            // 与 M3 AlertDialog 一致：标题 headlineSmall，正文 bodyMedium
                            androidx.compose.material3.LocalTextStyle provides MaterialTheme.typography.headlineSmall,
                        ) { it() }
                    }
                }
                text?.let {
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides textContentColor,
                        androidx.compose.material3.LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    ) { it() }
                }
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    dismissButton?.invoke()
                    androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
                    confirmButton()
                }
            }
        }
    }
}

@Composable
fun BasicAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: androidx.compose.ui.window.DialogProperties = androidx.compose.ui.window.DialogProperties(),
    content: @Composable () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Box(
            modifier = modifier
                .neumorphicDialogPlate()
                .neumorphicRaised(
                    shape = NeumorphicShape.xl,
                    colors = neumorphicColors,
                    spec = NeuElevation.dialog,
                ),
        ) { content() }
    }
}
