package io.github.aoguai.sesameag.ui.compose.neumorphic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.aoguai.sesameag.ui.theme.NeuElevation
import io.github.aoguai.sesameag.ui.theme.NeumorphicShape
import io.github.aoguai.sesameag.ui.theme.neumorphicColors
import io.github.aoguai.sesameag.ui.theme.neumorphicRaised
import io.github.aoguai.sesameag.ui.theme.neumorphicSunken
import io.github.aoguai.sesameag.ui.theme.neumorphicTinted

// ---------------------------------------------------------------------------
// 图标按钮
// ---------------------------------------------------------------------------

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    /** 是否用凸起底板包住图标；AppBar 里的图标按钮传 false 更轻 */
    elevated: Boolean = false,
    shape: RoundedCornerShape = NeumorphicShape.pill,
    content: @Composable () -> Unit,
) {
    val neu = neumorphicColors
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val base = if (elevated) {
        if (pressed) {
            Modifier.neumorphicSunken(shape = shape, colors = neu, spec = NeuElevation.button)
        } else {
            Modifier.neumorphicRaised(shape = shape, colors = neu, spec = NeuElevation.button)
        }
    } else {
        if (pressed) Modifier.neumorphicSunken(shape = shape, colors = neu, spec = NeuElevation.button) else Modifier
    }
    Box(
        modifier = modifier
            .size(48.dp)
            .then(base)
            .clip(shape)
            .androidClickable(enabled, source, onClick),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides if (enabled) neu.content else neu.contentMuted.copy(alpha = 0.4f),
            content = content,
        )
    }
}

/** 与 M3 FilledIconButton 同名：主色淡染的凸起按钮 */
@Composable
fun FilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.pill,
    colors: androidx.compose.material3.IconButtonColors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(),
    content: @Composable () -> Unit,
) {
    val neu = neumorphicColors
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.pill
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val tint = colors.containerColor.takeIf { it != Color.Unspecified }
        ?.copy(alpha = 0.2f)?.compositeOver(neu.background)
        ?: neu.accent.copy(alpha = 0.18f).compositeOver(neu.background)
    Box(
        modifier = modifier
            .size(48.dp)
            .then(
                if (pressed) {
                    Modifier.neumorphicSunken(shape = rounded, colors = neu, spec = NeuElevation.button)
                } else {
                    Modifier.neumorphicTinted(tint = tint, shape = rounded, colors = neu, spec = NeuElevation.button)
                },
            )
            .clip(rounded)
            .androidClickable(enabled, source, onClick),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides neu.accent,
            content = content,
        )
    }
}

/** 与 M3 SmallFloatingActionButton 同名 */
@Composable
fun SmallFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = NeumorphicShape.m,
    containerColor: Color = neumorphicColors.accent,
    contentColor: Color = neumorphicColors.onAccent,
    elevation: androidx.compose.material3.FloatingActionButtonElevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit,
) {
    val neu = neumorphicColors
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.m
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val tinted = containerColor.copy(alpha = if (pressed) 0.3f else 0.22f).compositeOver(neu.background)
    Box(
        modifier = modifier
            .size(40.dp)
            .then(
                if (pressed) {
                    Modifier.neumorphicSunken(shape = rounded, colors = neu, spec = NeuElevation.button)
                } else {
                    Modifier.neumorphicTinted(tint = tinted, shape = rounded, colors = neu, spec = NeuElevation.button)
                },
            )
            .clip(rounded)
            .androidClickable(true, source, onClick),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}

/** 与 M3 FloatingActionButton 同名 */
@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = NeumorphicShape.l,
    containerColor: Color = neumorphicColors.accent,
    contentColor: Color = neumorphicColors.onAccent,
    elevation: androidx.compose.material3.FloatingActionButtonElevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(),
    content: @Composable () -> Unit,
) {
    val neu = neumorphicColors
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.l
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val tinted = containerColor.copy(alpha = if (pressed) 0.3f else 0.22f).compositeOver(neu.background)
    Box(
        modifier = modifier
            .size(56.dp)
            .then(
                if (pressed) {
                    Modifier.neumorphicSunken(shape = rounded, colors = neu, spec = NeuElevation.button)
                } else {
                    Modifier.neumorphicTinted(tint = tinted, shape = rounded, colors = neu, spec = NeuElevation.button)
                },
            )
            .clip(rounded)
            .androidClickable(true, source, onClick),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}

// ---------------------------------------------------------------------------
// 开关 / 复选框
// ---------------------------------------------------------------------------

/**
 * 新拟态开关：轨道是浅凹槽（完整胶囊形），滑块是凸起的椭圆钮。
 *
 * 关键约束：
 * 1. 轨道必须是完整胶囊（圆角 = 高度/2），不能出现长方形底，因此圆角在绘制时按尺寸夹紧；
 * 2. 轨道不用强内阴影，只用很浅的下沉感 + 细内描边，避免在开启态露出一圈方框；
 * 3. 滑块比轨道矮 4dp，开启时向右滑到底，视觉上呈现「椭圆钮在凹槽里」。
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
    thumbContent: (@Composable () -> Unit)? = null,
) {
    val neu = neumorphicColors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val thumbOffset by animateDpAsState(if (checked) 24.dp else 2.dp, label = "thumbOffset")
    val thumbColor by animateColorAsState(if (checked) neu.accent else neu.light.copy(alpha = 1f), label = "thumbColor")
    val trackColor by animateColorAsState(
        if (checked) {
            neu.accent.copy(alpha = 0.22f).compositeOver(neu.background)
        } else {
            neu.sunken
        },
        label = "trackColor",
    )
    val toggleModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            interactionSource = source,
            indication = null,
            onValueChange = onCheckedChange,
        )
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(width = 54.dp, height = 32.dp)
            .clip(NeumorphicShape.pill)
            .background(trackColor)
            .switchTrackInset(neu.shadow, neu.light)
            .then(toggleModifier),
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset, top = 3.dp)
                .size(width = 26.dp, height = 26.dp)
                .neumorphicRaised(
                    shape = NeumorphicShape.pill,
                    colors = neu,
                    spec = NeuElevation.flat,
                    fill = thumbColor,
                )
                .clip(NeumorphicShape.pill),
            contentAlignment = Alignment.Center,
        ) {
            thumbContent?.let {
                CompositionLocalProvider(
                    LocalContentColor provides if (checked) neu.onAccent else neu.contentMuted,
                ) { it() }
            }
        }
    }
}

/**
 * 开关轨道的内部质感：极浅的内描边 + 下沿微高光，形成「凹槽」而不产生方框感。
 * 这里刻意不用 BlurMaskFilter，避免小尺寸元素被模糊边缘吃掉胶囊圆角。
 */
private fun Modifier.switchTrackInset(shadow: Color, light: Color): Modifier = drawBehind {
    val r = CornerRadius(size.height / 2f, size.height / 2f)
    // 上沿压暗：细描边
    drawRoundRect(
        color = shadow.copy(alpha = 0.32f),
        cornerRadius = r,
        style = Stroke(width = 1.4.dp.toPx()),
    )
    // 下沿透光：向内收 0.8dp 的极淡高光
    drawRoundRect(
        color = light.copy(alpha = 0.45f),
        topLeft = Offset(0.8.dp.toPx(), 0.8.dp.toPx()),
        size = Size(
            (size.width - 1.6.dp.toPx()).coerceAtLeast(1f),
            (size.height - 1.6.dp.toPx()).coerceAtLeast(1f),
        ),
        cornerRadius = CornerRadius(
            (size.height - 1.6.dp.toPx()).coerceAtLeast(1f) / 2f,
            (size.height - 1.6.dp.toPx()).coerceAtLeast(1f) / 2f,
        ),
        style = Stroke(width = 0.8.dp.toPx()),
    )
}

/**
 * 新拟态复选框：浅凹槽 + 细描边，勾选后槽内淡染主色并显示清晰的勾。
 *
 * 配色原则：
 * - 未勾选：底色用 [NeumorphicColors.track]（几乎等同底板），只靠 1dp 细描边表达「可勾选」，
 *   不再用深色填充，避免被误读成已勾选；
 * - 已勾选：主色淡染（约 26%），勾号用主色深调，既醒目又不会糊成一块深色方块；
 * - 内阴影降到极淡，只保留一点点下沉感。
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: androidx.compose.material3.CheckboxColors = androidx.compose.material3.CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val neu = neumorphicColors
    val ink = checkInk()
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val fill by animateColorAsState(
        targetValue = if (checked) {
            // 18% 淡染：既有「已选中」的色彩提示，又不会变成深色方块
            neu.accent.copy(alpha = 0.18f).compositeOver(neu.background)
        } else {
            neu.track
        },
        label = "checkboxFill",
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) neu.accent.copy(alpha = 0.45f) else neu.hairline,
        label = "checkboxBorder",
    )
    val checkColor by animateColorAsState(
        // 勾号用主色的深调，保证在淡染底上足够清晰（对比度约 6.8:1）
        targetValue = if (checked) ink else Color.Transparent,
        label = "checkboxCheck",
    )
    val toggleModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = source,
            indication = null,
            onValueChange = onCheckedChange,
        )
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(48.dp)
            .then(toggleModifier),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(fill)
                // 细描边替代深色内阴影，边缘柔和不发黑
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .softInnerHighlight(neu),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = checkColor,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

/**
 * 极小尺寸控件的内侧高光：只在下沿画一道极淡的白线，营造轻微凸起的边缘，
 * 不做模糊也不压暗，避免小方块被内阴影吃掉细节。
 */
@Composable
private fun Modifier.softInnerHighlight(
    neu: io.github.aoguai.sesameag.ui.theme.NeumorphicColors,
): Modifier = drawBehind {
    val corner = CornerRadius(8.dp.toPx(), 8.dp.toPx())
    // 上沿极淡压暗
    drawRoundRect(
        color = neu.shadow.copy(alpha = 0.16f),
        cornerRadius = corner,
        style = Stroke(width = 1.dp.toPx()),
    )
    // 下沿极淡高光
    drawRoundRect(
        color = neu.light.copy(alpha = 0.55f),
        topLeft = Offset(0f, 0.9.dp.toPx()),
        size = Size(size.width, (size.height - 0.9.dp.toPx()).coerceAtLeast(1f)),
        cornerRadius = corner,
        style = Stroke(width = 0.8.dp.toPx()),
    )
}

/**
 * 新拟态单选按钮：浅圆槽 + 细描边，选中时显示柔和淡染内圆。
 * 与 [Checkbox] 保持同一套「浅底 + 细边 + 淡染选中」的规则。
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: androidx.compose.material3.RadioButtonColors = androidx.compose.material3.RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val neu = neumorphicColors
    val ink = checkInk()
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val clickModifier = if (onClick != null) {
        Modifier.androidClickable(enabled, source, onClick)
    } else {
        Modifier
    }
    val ringColor by animateColorAsState(
        targetValue = if (selected) neu.accent.copy(alpha = 0.55f) else neu.hairline,
        label = "radioRing",
    )
    Box(
        modifier = modifier
            .size(48.dp)
            .then(clickModifier),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(NeumorphicShape.pill)
                .background(neu.track)
                .border(1.dp, ringColor, NeumorphicShape.pill),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    Modifier
                        .size(11.dp)
                        .clip(NeumorphicShape.pill)
                        .background(ink),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 芯片
// ---------------------------------------------------------------------------

/**
 * 新拟态筛选芯片（与 M3 FilterChip 同名）。
 * 未选中为浅凸起，选中为主色淡染凸起，避免 M3 默认的深色实心块。
 */
@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    shape: Shape = NeumorphicShape.pill,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    elevation: SelectableChipElevation? = FilterChipDefaults.filterChipElevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val neu = neumorphicColors
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.pill
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val tint = if (selected) neu.accent.copy(alpha = 0.2f).compositeOver(neu.background) else neu.background
    Box(
        modifier = modifier
            .heightIn(min = 40.dp)
            .then(
                if (pressed || selected) {
                    Modifier.neumorphicSunken(shape = rounded, colors = neu, spec = NeuElevation.sunken, fill = tint)
                } else {
                    Modifier.neumorphicRaised(shape = rounded, colors = neu, spec = NeuElevation.flat, fill = tint)
                },
            )
            .clip(rounded)
            .androidClickable(enabled, source, onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (selected) neu.accent else neu.content,
                androidx.compose.material3.LocalTextStyle provides MaterialTheme.typography.labelLarge,
            ) {
                leadingIcon?.let {
                    Box(Modifier.padding(end = 6.dp)) { it() }
                }
                label()
                trailingIcon?.let {
                    Box(Modifier.padding(start = 6.dp)) { it() }
                }
            }
        }
    }
}

/** 与 M3 AssistChip 同名 */
@Composable
fun AssistChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    shape: Shape = NeumorphicShape.pill,
    colors: androidx.compose.material3.ChipColors = androidx.compose.material3.AssistChipDefaults.assistChipColors(),
    elevation: SelectableChipElevation? = null,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
) = FilterChip(
    selected = false,
    onClick = onClick,
    label = label,
    modifier = modifier,
    enabled = enabled,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    shape = shape,
    elevation = elevation,
    border = border,
    interactionSource = interactionSource,
)

/** 与 M3 SuggestionChip 同名 */
@Composable
fun SuggestionChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = NeumorphicShape.pill,
    colors: androidx.compose.material3.ChipColors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(),
    elevation: SelectableChipElevation? = null,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
) = FilterChip(
    selected = false,
    onClick = onClick,
    label = label,
    modifier = modifier,
    enabled = enabled,
    shape = shape,
    elevation = elevation,
    border = border,
    interactionSource = interactionSource,
)

/** 统一封装 clickable，避免各处 indication 水波纹破坏新拟态质感 */
@Composable
internal fun Modifier.androidClickable(
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
): Modifier = this.then(
    clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick,
    ),
)

/**
 * 勾号专用色：在淡染底上保证清晰可辨。
 *
 * 直接拿主色当勾号，在 18% 淡染底上对比度只有约 2.9:1。
 * 浅色主题下压暗（深墨），深色主题下提亮（浅亮），两者都能到 6:1 以上。
 */
@Composable
private fun checkInk(): Color {
    val neu = neumorphicColors
    val isLight = neu.background.luminance() > 0.5f
    return if (isLight) {
        Color(red = neu.accent.red * 0.55f, green = neu.accent.green * 0.55f, blue = neu.accent.blue * 0.55f)
    } else {
        // 深色底上往白方向提亮，避免勾号糊进底色
        Color(
            red = (neu.accent.red * 0.5f + 0.5f).coerceIn(0f, 1f),
            green = (neu.accent.green * 0.5f + 0.5f).coerceIn(0f, 1f),
            blue = (neu.accent.blue * 0.5f + 0.5f).coerceIn(0f, 1f),
        )
    }
}
