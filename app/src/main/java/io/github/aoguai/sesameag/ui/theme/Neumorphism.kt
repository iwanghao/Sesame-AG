package io.github.aoguai.sesameag.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * 新拟态（Neumorphism）设计令牌。
 *
 * 视觉核心：整页共用同一块柔和底色，元素靠「右下暗影 + 左上高光」凸起，
 * 或靠「左上暗影 + 右下高光」的内阴影凹陷，从而像从背景中挤出/压入。
 * 所有页面都只从这里取色与圆角，才能保证页面内、页面间风格一致。
 */
@Immutable
data class NeumorphicColors(
    /** 页面与卡片的统一底色，所有元素阴影都以它作为参照 */
    val background: Color,
    /** 凸起元素右下方的暗影色 */
    val shadow: Color,
    /** 凸起元素左上方的高光色 */
    val light: Color,
    /** 强调色 */
    val accent: Color,
    /** 强调色上的前景色 */
    val onAccent: Color,
    /** 主文字色 */
    val content: Color,
    /** 次级文字色 */
    val contentMuted: Color,
    /** 错误色 */
    val error: Color,
    /** 凹陷内部底色（比背景略深，强化下沉感） */
    val sunken: Color,
    /** 凸起元素本体色（默认与背景一致，形成「同色挤出」） */
    val raised: Color,
    /**
     * 输入框内底：只比底板暗一点点的一档浅色。
     * 比 [sunken] 更亮，保证输入框看起来是「浅浅凹下去」而不是「挖了个深坑」。
     */
    val field: Color,
    /**
     * 勾选框/小凹槽内底：同样只比底板略深，
     * 未勾选时不能出现明显深色块，否则会被误读成「已填充」。
     */
    val track: Color,
    /** 输入框/勾选框的柔和描边色，远弱于 [shadow] */
    val hairline: Color,
)

private val LightNeumorphicColors = NeumorphicColors(
    background = NeumorphicBackgroundLight,
    shadow = NeumorphicShadowLight,
    light = NeumorphicLightLight,
    accent = primaryLight,
    onAccent = onPrimaryLight,
    content = onSurfaceLight,
    contentMuted = onSurfaceVariantLight,
    error = errorLight,
    sunken = Color(0xFFE3E7EA),
    raised = NeumorphicBackgroundLight,
    // 输入框内底：比底板(232,236,239)仅暗一档，视觉上是浅凹槽而非深坑
    field = Color(0xFFE4E8EB),
    // 勾选框内底：几乎与底板同色，仅靠细描边体现「可勾选」
    track = Color(0xFFE6EAED),
    // 柔和描边：介于底板与阴影之间，远浅于原先的 A9B3BC
    hairline = Color(0xFFCBD3D9),
)

private val DarkNeumorphicColors = NeumorphicColors(
    background = NeumorphicBackgroundDark,
    shadow = NeumorphicShadowDark,
    light = NeumorphicLightDark,
    accent = primaryDark,
    onAccent = Color(0xFF0E2618),
    content = onSurfaceDark,
    contentMuted = onSurfaceVariantDark,
    error = errorDark,
    sunken = Color(0xFF191D21),
    raised = NeumorphicBackgroundDark,
    field = Color(0xFF2C3339),
    track = Color(0xFF2E353C),
    hairline = Color(0xFF414B54),
)

/** 全局新拟态圆角，避免卡片 8dp、按钮 16dp 的割裂感 */
object NeumorphicShape {
    val xs: RoundedCornerShape = RoundedCornerShape(10.dp)
    val s: RoundedCornerShape = RoundedCornerShape(14.dp)
    val m: RoundedCornerShape = RoundedCornerShape(18.dp)
    val l: RoundedCornerShape = RoundedCornerShape(24.dp)
    val xl: RoundedCornerShape = RoundedCornerShape(32.dp)
    val pill: RoundedCornerShape = RoundedCornerShape(percent = 50)
}

/** 新拟态阴影参数：offset 越小越「贴地」，blur 越大越柔和 */
@Immutable
data class NeumorphicSpec(
    val offset: Dp = 6.dp,
    val blur: Dp = 10.dp,
    val shadowAlpha: Float = 0.55f,
    val lightAlpha: Float = 1f,
)

object NeuElevation {
    /**
     * 所有档位共用柔和高光：高光只负责「提亮边缘」，不能变成一圈白边。
     * 之前 lightAlpha 取 1f（纯白），在浅色底板上会形成过亮的发光描边，
     * 这里统一压到 0.62，视觉上更接近真实柔光。
     */
    private const val SOFT_LIGHT = 0.62f

    /** 轻元素：小组件、字块 */
    val flat = NeumorphicSpec(offset = 2.dp, blur = 5.dp, shadowAlpha = 0.32f, lightAlpha = 0.5f)

    /** 常规卡片 */
    val raised = NeumorphicSpec(offset = 6.dp, blur = 12.dp, shadowAlpha = 0.42f, lightAlpha = SOFT_LIGHT)

    /** 强调卡片 / 大面积容器（用于弹窗浮板，不再用最高档的强投影+强高光） */
    val high = NeumorphicSpec(offset = 7.dp, blur = 18.dp, shadowAlpha = 0.42f, lightAlpha = 0.5f)

    /** 按钮默认 */
    val button = NeumorphicSpec(offset = 4.dp, blur = 9.dp, shadowAlpha = 0.5f, lightAlpha = 0.58f)

    /** 输入框、滑轨等凹陷 */
    val sunken = NeumorphicSpec(offset = 3.dp, blur = 6.dp, shadowAlpha = 0.5f, lightAlpha = 0.55f)

    /**
     * 弹窗浮板专用：投影略深以拉开层级，但高光刻意压到 0.36，
     * 避免在遮罩之上出现一圈发光的白边。
     */
    val dialog = NeumorphicSpec(offset = 7.dp, blur = 20.dp, shadowAlpha = 0.34f, lightAlpha = 0.36f)
}

private val LocalNeumorphicColors = staticCompositionLocalOf { LightNeumorphicColors }

/** 当前主题下的新拟态令牌 */
val neumorphicColors: NeumorphicColors
    @Composable
    @ReadOnlyComposable
    get() = LocalNeumorphicColors.current

@Composable
fun ProvideNeumorphicColors(darkTheme: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalNeumorphicColors provides if (darkTheme) DarkNeumorphicColors else LightNeumorphicColors,
        content = content,
    )
}

/**
 * 凸起效果：右下模糊暗影 + 左上模糊高光。用于卡片、按钮、导航等。
 * 绘制时会先把底色填充一遍，因此可以直接挂在一个透明的 Box 上。
 */
@Composable
fun Modifier.neumorphicRaised(
    shape: RoundedCornerShape = NeumorphicShape.m,
    colors: NeumorphicColors = neumorphicColors,
    spec: NeumorphicSpec = NeuElevation.raised,
    fill: Color = colors.raised,
): Modifier = composed { drawNeumorphic(shape, colors, spec, fill, inset = false) }

/**
 * 凹陷效果：左上模糊暗影 + 右下模糊高光，内阴影画在内部。用于输入框、进度槽、按下态。
 */
@Composable
fun Modifier.neumorphicSunken(
    shape: RoundedCornerShape = NeumorphicShape.m,
    colors: NeumorphicColors = neumorphicColors,
    spec: NeumorphicSpec = NeuElevation.sunken,
    fill: Color = colors.sunken,
): Modifier = composed { drawNeumorphic(shape, colors, spec, fill, inset = true) }

/**
 * 凸起 + 主色染色：用于主按钮、选中态芯片等需要强调的元素。
 */
@Composable
fun Modifier.neumorphicTinted(
    tint: Color,
    shape: RoundedCornerShape = NeumorphicShape.m,
    colors: NeumorphicColors = neumorphicColors,
    spec: NeumorphicSpec = NeuElevation.button,
): Modifier = composed { drawNeumorphic(shape, colors, spec, tint, inset = false) }

private fun Modifier.drawNeumorphic(
    shape: RoundedCornerShape,
    colors: NeumorphicColors,
    spec: NeumorphicSpec,
    fill: Color,
    inset: Boolean,
): Modifier = this.drawBehind {
    val offset = spec.offset.toPx()
    val blur = spec.blur.toPx()
    // 关键：圆角必须按「元素实际尺寸」夹紧到半边短边，否则胶囊形（开关轨道）
    // 会因为半径超过高度而退化成带硬边的圆角矩形，看起来像一块长方形底。
    val maxCorner = min(size.width, size.height) / 2f
    val corner = shape.topStart.toPx(size, this).coerceIn(0f, maxCorner)
    val cRadius = CornerRadius(corner, corner)

    if (inset) {
        // 凹面：先填充内底
        drawRoundRect(color = fill, cornerRadius = cRadius)
        // 内阴影用「描边」绘制：外轮廓向内偏移 offset 后只画边缘，
        // 模糊后只在靠边的窄带内产生暗/亮渐变，中心区域保持干净。
        val insetStroke = (offset + blur * 0.35f).coerceAtLeast(1f)
        drawBlurredStroke(
            path = roundPath(offset, offset, size.width - offset * 2f, size.height - offset * 2f, corner),
            color = colors.shadow.copy(alpha = spec.shadowAlpha * 0.85f),
            blur = blur,
            strokeWidth = insetStroke,
        )
        drawBlurredStroke(
            path = roundPath(-offset, -offset, size.width + offset * 2f, size.height + offset * 2f, corner),
            color = colors.light.copy(alpha = spec.lightAlpha * 0.55f),
            blur = blur * 0.8f,
            strokeWidth = insetStroke,
        )
    } else {
        // 凸面：先画两个模糊影子，再覆盖本体
        drawBlurredStroke(
            path = roundPath(offset, offset, size.width, size.height, corner),
            color = colors.shadow.copy(alpha = spec.shadowAlpha),
            blur = blur,
            strokeWidth = 0f,
        )
        drawBlurredStroke(
            path = roundPath(-offset, -offset, size.width, size.height, corner),
            color = colors.light.copy(alpha = spec.lightAlpha),
            blur = blur,
            strokeWidth = 0f,
        )
        // 本体色覆盖中心，形成「同色挤出」
        if (fill.alpha > 0f) {
            drawRoundRect(color = fill, cornerRadius = cRadius)
        }
    }
}

private fun roundPath(left: Float, top: Float, width: Float, height: Float, corner: Float): Path {
    // 半径同样要夹紧，避免窄条元素半径溢出导致路径自交
    val safeCorner = corner.coerceIn(0f, maxOf(min(width, height), 0f) / 2f)
    return Path().apply {
        addRoundRect(
            RoundRect(
                left = left,
                top = top,
                right = left + width,
                bottom = top + height,
                radiusX = safeCorner,
                radiusY = safeCorner,
            ),
        )
    }
}

/**
 * 用系统 BlurMaskFilter 画一层模糊路径，得到真正柔和的新拟态阴影。
 * strokeWidth > 0 时只画边缘（用于内阴影），否则填充整块（用于外投影）。
 */
private fun DrawScope.drawBlurredStroke(
    path: Path,
    color: Color,
    blur: Float,
    strokeWidth: Float,
) {
    if (blur <= 0.01f || color.alpha <= 0f) return
    drawIntoCanvas { canvas ->
        val native = canvas.nativeCanvas
        val paint = android.graphics.Paint().apply {
            this.color = color.toArgb()
            isAntiAlias = true
            maskFilter = BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL)
            if (strokeWidth > 0f) {
                style = android.graphics.Paint.Style.STROKE
                this.strokeWidth = strokeWidth
            }
        }
        native.drawPath(path.asAndroidPath(), paint)
    }
}

/**
 * 焦点态主色光环，给凹陷输入框/可聚焦元素提供明确的视觉反馈。
 */
@Composable
fun Modifier.neumorphicFocusRing(
    focused: Boolean,
    shape: RoundedCornerShape = NeumorphicShape.m,
    color: Color = neumorphicColors.accent,
    width: Dp = 2.dp,
): Modifier = if (!focused) this else this.drawBehind {
    drawRoundRect(
        color = color.copy(alpha = 0.65f),
        cornerRadius = CornerRadius(shape.topStart.toPx(size, this)),
        style = Stroke(width = width.toPx()),
    )
}
