package io.github.aoguai.sesameag.ui.compose.neumorphic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button as M3Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import io.github.aoguai.sesameag.ui.theme.NeuElevation
import io.github.aoguai.sesameag.ui.theme.NeumorphicShape
import io.github.aoguai.sesameag.ui.theme.neumorphicColors
import io.github.aoguai.sesameag.ui.theme.neumorphicFocusRing
import io.github.aoguai.sesameag.ui.theme.neumorphicRaised
import io.github.aoguai.sesameag.ui.theme.neumorphicSunken
import io.github.aoguai.sesameag.ui.theme.neumorphicTinted

// ---------------------------------------------------------------------------
// 新拟态按钮
//
// 思路：按钮不再使用 M3 的填充色块，而是「同色挤出 + 深色文字」。
// 主按钮用主色淡染的新拟态凸起，次按钮（TextButton）用凹陷或透明+按下凹陷。
// 所有按钮都保留最小 48dp 触控高度，避免破坏可点击性。
// ---------------------------------------------------------------------------

/** 新拟态按钮的视觉层级 */
enum class NeuButtonStyle {
    /** 主色淡染凸起，用于页面主操作 */
    Accent,

    /** 同色凸起，用于次要操作 */
    Raised,

    /** 凹陷，用于「已选中 / 按下」或次级强调 */
    Sunken,

    /** 透明，仅悬浮时凸起，用于文字按钮 */
    Ghost,
}

private fun neuButtonContainer(style: NeuButtonStyle, accent: Color, background: Color): Color =
    when (style) {
        NeuButtonStyle.Accent -> accent.copy(alpha = 0.18f).compositeOver(background)
        NeuButtonStyle.Raised -> background
        NeuButtonStyle.Sunken -> background
        NeuButtonStyle.Ghost -> Color.Transparent
    }

@Composable
fun NeuButtonBase(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: NeuButtonStyle = NeuButtonStyle.Raised,
    shape: RoundedCornerShape = NeumorphicShape.m,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    interactionSource: MutableInteractionSource? = null,
    /** 自定义染色：不传时按 style 自动取主色 */
    tint: Color? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = neumorphicColors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val baseContainer = neuButtonContainer(style, colors.accent, colors.background)
    val container = tint?.takeIf { style == NeuButtonStyle.Accent }
        ?.copy(alpha = 0.18f)?.compositeOver(colors.background)
        ?: baseContainer
    val containerColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (pressed && style != NeuButtonStyle.Ghost) {
            colors.sunken
        } else {
            container
        },
        label = "neuButtonColor",
    )
    val surfaceModifier = when {
        pressed -> Modifier.neumorphicSunken(shape = shape, colors = colors, spec = NeuElevation.button, fill = containerColor)
        style == NeuButtonStyle.Ghost -> Modifier
        style == NeuButtonStyle.Accent -> Modifier.neumorphicTinted(
            tint = containerColor,
            shape = shape,
            colors = colors,
            spec = NeuElevation.button,
        )
        else -> Modifier.neumorphicRaised(shape = shape, colors = colors, spec = NeuElevation.button, fill = containerColor)
    }

    M3Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .then(surfaceModifier)
            .clip(shape),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = if (style == NeuButtonStyle.Accent) colors.accent else colors.content,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = colors.contentMuted.copy(alpha = 0.5f),
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        border = null,
        contentPadding = contentPadding,
        interactionSource = source,
        content = content,
    )
}

/** 主按钮：主色淡染的新拟态凸起 */
@Composable
fun NeuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: NeuButtonStyle = NeuButtonStyle.Accent,
    shape: RoundedCornerShape = NeumorphicShape.m,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit,
) = NeuButtonBase(
    onClick = onClick,
    modifier = modifier,
    enabled = enabled,
    style = style,
    shape = shape,
    contentPadding = contentPadding,
    content = content,
)

// ---------------------------------------------------------------------------
// 新拟态卡片 / 容器
// ---------------------------------------------------------------------------

/**
 * 通用新拟态容器：同色凸起 + 大圆角。
 * 这是页面里最常用的「卡片」，取代 M3 Card / Surface 的 surfaceContainer 系列。
 */
@Composable
fun NeuSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = NeumorphicShape.l,
    /** 凸起、凹陷或扁平 */
    raised: Boolean = true,
    sunken: Boolean = false,
    fill: Color = neumorphicColors.background,
    spec: io.github.aoguai.sesameag.ui.theme.NeumorphicSpec = NeuElevation.raised,
    content: @Composable () -> Unit,
) {
    val colors = neumorphicColors
    val foundation = when {
        sunken -> Modifier.neumorphicSunken(shape = shape, colors = colors, spec = NeuElevation.sunken, fill = fill)
        raised -> Modifier.neumorphicRaised(shape = shape, colors = colors, spec = spec, fill = fill)
        else -> Modifier
    }
    Box(modifier = modifier.then(foundation).clip(shape)) {
        content()
    }
}

/** 新拟态卡片：可点击时按下会「陷进去」 */
@Composable
fun NeuCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: RoundedCornerShape = NeumorphicShape.l,
    /** 选中态：主色淡染，用于列表中被选中的条目 */
    selected: Boolean = false,
    selectedColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val colors = neumorphicColors
    val tint = selectedColor ?: colors.accent
    val fill = if (selected) tint.copy(alpha = 0.16f).compositeOver(colors.background) else colors.background
    if (onClick == null) {
        NeuSurface(modifier = modifier, shape = shape, fill = fill) { content() }
        return
    }
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val container by androidx.compose.animation.animateColorAsState(
        targetValue = if (pressed) colors.sunken else fill,
        label = "neuCardColor",
    )
    val foundation = if (pressed) {
        Modifier.neumorphicSunken(shape = shape, colors = colors, spec = NeuElevation.sunken, fill = container)
    } else {
        Modifier.neumorphicRaised(shape = shape, colors = colors, spec = NeuElevation.raised, fill = container)
    }
    Box(
        modifier = modifier
            .then(foundation)
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = source,
                indication = null,
                onClick = onClick,
            ),
    ) {
        content()
    }
}

// ---------------------------------------------------------------------------
// 与 M3 同名的包装按钮，方便页面仅替换 import 即可整体切换风格。
// 保留 M3 的参数以兼容现有调用点（包括 ButtonDefaults.xxxButtonColors(...)）。
// ---------------------------------------------------------------------------

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.m,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val neu = neumorphicColors
    // 调用方显式指定了容器色时（如 error 确认按钮），沿用它做淡染，保留原有语义
    val accent = colors.containerColor.takeIf { it != Color.Unspecified }
    NeuButtonBase(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        style = NeuButtonStyle.Accent,
        shape = if (shape is RoundedCornerShape) shape else NeumorphicShape.m,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.m,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    NeuButtonBase(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        style = NeuButtonStyle.Raised,
        shape = if (shape is RoundedCornerShape) shape else NeumorphicShape.m,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            CompositionLocalProvider(LocalContentColor provides neumorphicColors.content) {
                content()
            }
        },
    )
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.s,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val neu = neumorphicColors
    val tint = colors.contentColor.takeIf { it != Color.Unspecified } ?: neu.accent
    NeuButtonBase(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        style = NeuButtonStyle.Ghost,
        shape = if (shape is RoundedCornerShape) shape else NeumorphicShape.s,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = {
            CompositionLocalProvider(LocalContentColor provides tint) {
                content()
            }
        },
    )
}

@Composable
fun FilledTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.m,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    NeuButtonBase(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        style = NeuButtonStyle.Accent,
        shape = if (shape is RoundedCornerShape) shape else NeumorphicShape.m,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}
