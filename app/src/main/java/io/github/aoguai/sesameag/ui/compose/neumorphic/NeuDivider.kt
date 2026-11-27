package io.github.aoguai.sesameag.ui.compose.neumorphic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.aoguai.sesameag.ui.theme.neumorphicColors

/**
 * 新拟态分隔线。
 *
 * 普通 1px 直线在柔和阴影里会显得过于锐利，这里改成「上沿暗、下沿亮」的双色细线，
 * 视觉上像一道浅槽，既能分隔内容又不会破坏材质统一。
 */
@Composable
fun NeuDivider(
    modifier: Modifier = Modifier,
) {
    val neu = neumorphicColors
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(neu.shadow.copy(alpha = 0.38f)),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(neu.light.copy(alpha = 0.7f)),
        )
    }
}

/** 竖向分隔线，用于横向排列的分栏之间 */
@Composable
fun NeuVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 2.dp,
) {
    val neu = neumorphicColors
    Row(modifier = modifier) {
        Box(
            Modifier
                .fillMaxHeight()
                .width(thickness / 2)
                .background(neu.shadow.copy(alpha = 0.38f)),
        )
        Box(
            Modifier
                .fillMaxHeight()
                .width(thickness / 2)
                .background(neu.light.copy(alpha = 0.7f)),
        )
    }
}
