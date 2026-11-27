package io.github.aoguai.sesameag.ui.compose.neumorphic

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.aoguai.sesameag.ui.theme.NeumorphicShape
import io.github.aoguai.sesameag.ui.theme.neumorphicColors
import io.github.aoguai.sesameag.ui.theme.neumorphicFocusRing
import io.github.aoguai.sesameag.ui.theme.neumorphicSunken

/**
 * 新拟态输入框（与 M3 OutlinedTextField 同名）。
 *
 * 输入框是新拟态里唯一必须「凹」的元素：用内阴影表现被压入底板的槽位，
 * 聚焦时叠加主色光环，保证键盘输入时有明确的焦点反馈。
 * 参数保持与 M3 一致的关键子集，现有调用点无需改动语义。
 */
@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = NeumorphicShape.m,
) {
    val neu = neumorphicColors
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val rounded = if (shape is RoundedCornerShape) shape else NeumorphicShape.m
    val accent = if (isError) neu.error else neu.accent

    androidx.compose.foundation.layout.Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(rounded)
                // 内底用很浅的一档（比底板仅暗一点点），不再用深色凹陷
                .background(if (isError) neu.error.copy(alpha = 0.06f).compositeOver(neu.field) else neu.field)
                // 细描边：常态用柔和 hairline，聚焦/报错时才提亮为主色
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> neu.error.copy(alpha = 0.55f)
                        focused -> accent.copy(alpha = 0.60f)
                        else -> neu.hairline
                    },
                    shape = rounded,
                )
                // 聚焦时外圈柔和光环，替代生硬的高对比边框
                .neumorphicFocusRing(focused = focused, shape = rounded, color = accent.copy(alpha = 0.55f))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            androidx.compose.foundation.layout.Column {
                if (label != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides if (focused) accent else neu.contentMuted,
                        androidx.compose.material3.LocalTextStyle provides MaterialTheme.typography.labelMedium,
                    ) {
                        label()
                    }
                }
                Box(contentAlignment = Alignment.CenterStart) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        leadingIcon?.let { Box(Modifier.padding(end = 8.dp)) { it() } }
                        prefix?.let { Box(Modifier.padding(end = 4.dp)) { it() } }
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            modifier = Modifier.weight(1f),
                            enabled = enabled,
                            readOnly = readOnly,
                            textStyle = textStyle.copy(color = if (enabled) neu.content else neu.contentMuted),
                            cursorBrush = SolidColor(accent),
                            visualTransformation = visualTransformation,
                            keyboardOptions = keyboardOptions,
                            keyboardActions = keyboardActions,
                            singleLine = singleLine,
                            maxLines = maxLines,
                            minLines = minLines,
                            interactionSource = source,
                            decorationBox = { inner ->
                                androidx.compose.foundation.layout.Box {
                                    if (value.isEmpty() && placeholder != null) {
                                        androidx.compose.runtime.CompositionLocalProvider(
                                            LocalContentColor provides neu.contentMuted.copy(alpha = 0.6f),
                                            androidx.compose.material3.LocalTextStyle provides textStyle,
                                        ) {
                                            placeholder()
                                        }
                                    }
                                    inner()
                                }
                            },
                        )
                        suffix?.let { Box(Modifier.padding(start = 4.dp)) { it() } }
                        trailingIcon?.let { Box(Modifier.padding(start = 8.dp)) { it() } }
                    }
                }
            }
        }
        if (supportingText != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 4.dp),
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides if (isError) neu.error else neu.contentMuted,
                ) { supportingText() }
            }
        }
    }
}

/**
 * 与 M3 TextField 同名：新拟态下与 OutlinedTextField 表现为同一种凹陷槽，
 * 保证页面里「搜索框」和「配置项」看起来是同一套控件。
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    supportingText: (@Composable () -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = NeumorphicShape.m,
) = OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    textStyle = textStyle,
    label = label,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    prefix = prefix,
    suffix = suffix,
    supportingText = supportingText,
    isError = isError,
    visualTransformation = visualTransformation,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    singleLine = singleLine,
    maxLines = maxLines,
    minLines = minLines,
    interactionSource = interactionSource,
    shape = shape,
)

/** 供自定义控件复用一个新拟态凹槽外框 */
@Composable
fun NeuFieldShell(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = NeumorphicShape.m,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    // 与 OutlinedTextField 同一套「浅内底 + 细描边」，避免自定义控件与标准输入框割裂
    Box(
        modifier = modifier
            .clip(shape)
            .background(neumorphicColors.field)
            .border(1.dp, neumorphicColors.hairline, shape)
            .padding(contentPadding),
        content = content,
    )
}

/** 新拟态风格的提示文字，统一次级文案颜色 */
@Composable
fun NeuHintText(text: String, modifier: Modifier = Modifier, color: Color = neumorphicColors.contentMuted) {
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.bodySmall, color = color)
}
