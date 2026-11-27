package io.github.aoguai.sesameag.ui.compose

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.aoguai.sesameag.ui.compose.neumorphic.AlertDialog
import io.github.aoguai.sesameag.ui.compose.neumorphic.TextButton
import io.github.aoguai.sesameag.ui.compose.neumorphic.NeuButtonBase
import io.github.aoguai.sesameag.ui.compose.neumorphic.NeuButtonStyle
import io.github.aoguai.sesameag.ui.extension.parseHtml
import io.github.aoguai.sesameag.ui.theme.NeumorphicShape
import io.github.aoguai.sesameag.ui.theme.neumorphicColors

@Composable
fun CommonAlertDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String, // C++ 传来的原始 HTML 字符串
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    confirmText: String = "确认",
    dismissText: String = "取消",
    confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
    showCancelButton: Boolean = true
) {
    if (showDialog) {
        val neu = neumorphicColors
        // 确认按钮区分「普通操作」与「危险操作」：危险操作用更低饱和的红，避免刺眼
        val isDanger = confirmButtonColor == MaterialTheme.colorScheme.error
        val accent = if (isDanger) {
            neu.error.copy(alpha = 0.85f).compositeOver(neu.background)
        } else {
            confirmButtonColor
        }
        AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = icon?.let { { Icon(it, null, tint = if (isDanger) neu.error else iconTint) } },
            title = { Text(text = title) },
            text = {
                Text(
                    text = text.parseHtml(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = neu.contentMuted,
                )
            },
            confirmButton = {
                // 主操作用低饱和淡染的凸起按钮，与页面里的按钮同材质
                NeuButtonBase(
                    onClick = {
                        onConfirm()
                        onDismissRequest()
                    },
                    style = NeuButtonStyle.Accent,
                    shape = NeumorphicShape.s,
                    tint = accent,
                ) {
                    Text(confirmText, color = if (isDanger) neu.error else neu.accent)
                }
            },
            dismissButton = if (showCancelButton) {
                {
                    TextButton(onClick = onDismissRequest) {
                        Text(dismissText)
                    }
                }
            } else null,
            containerColor = neu.background,
            textContentColor = neu.content,
        )
    }
}
