package io.github.aoguai.sesameag.ui.compose.neumorphic

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import io.github.aoguai.sesameag.ui.theme.neumorphicColors

/**
 * 新拟态弹窗的遮罩强度。
 *
 * 系统默认 scrim 近黑且不透明，压在浅色同色底板上会显得又黑又硬。
 * 这里统一压到很低：浅色主题 0.18 的冷灰，深色主题 0.32 的纯黑。
 */
private const val SCRIM_ALPHA_LIGHT = 0.18f
private const val SCRIM_ALPHA_DARK = 0.32f

/** 弹窗默认窗口属性：保留返回键 / 点击外部关闭，以及平台默认宽度 */
val NeuDialogProperties: DialogProperties
    get() = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        usePlatformDefaultWidth = true,
    )

/**
 * 把弹窗窗口的系统遮罩调到柔和值。
 *
 * Compose Dialog 的 View 树挂在一个 ComponentDialog 窗口里，
 * 通过 [LocalView] 的 Context 链可以直接拿到该 Dialog 并改写 dimAmount，
 * 无需反射，也不影响任何交互行为。
 */
@Composable
fun Modifier.neumorphicDialogPlate(): Modifier = this.composed {
    val view = LocalView.current
    val isLight = neumorphicColors.background.luminance() > 0.5f
    SideEffect {
        val dialog = view.context.findDialog()
        val window = dialog?.window ?: return@SideEffect
        val alpha = if (isLight) SCRIM_ALPHA_LIGHT else SCRIM_ALPHA_DARK
        runCatching {
            window.setDimAmount(alpha)
            val attrs = window.attributes
            attrs.dimAmount = alpha
            window.attributes = attrs
        }
    }
    this
}

/** 沿 Context 包装链向上找到承载弹窗的 Dialog */
private fun Context.findDialog(): android.app.Dialog? {
    var ctx: Context? = this
    var guard = 0
    while (ctx != null && guard++ < 12) {
        if (ctx is android.app.Dialog) return ctx
        ctx = (ctx as? ContextWrapper)?.baseContext
    }
    return null
}
