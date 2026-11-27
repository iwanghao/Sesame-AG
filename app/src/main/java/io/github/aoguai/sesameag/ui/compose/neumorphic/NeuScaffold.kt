package io.github.aoguai.sesameag.ui.compose.neumorphic

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.github.aoguai.sesameag.ui.theme.neumorphicColors

/**
 * 新拟态顶栏配色。
 *
 * 顶栏不再是 M3 默认的 tinted surface，而是与页面底板完全同色，
 * 通过下方的留白与卡片阴影区分层级，避免出现「顶部一条灰带」的割裂感。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun neuTopAppBarColors(): TopAppBarColors {
    val neu = neumorphicColors
    return TopAppBarDefaults.topAppBarColors(
        containerColor = neu.background,
        scrolledContainerColor = neu.background,
        titleContentColor = neu.content,
        actionIconContentColor = neu.content,
        navigationIconContentColor = neu.content,
    )
}

/**
 * 新拟态页面骨架：统一 Scaffold 底板与顶栏配色。
 *
 * 页面里用它替换原本的 `Scaffold(topBar = { TopAppBar(...) })`，
 * 功能、内容与布局结构保持不变，只把材质对齐到全局底板。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuScaffold(
    title: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    topBar: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
) {
    val neu = neumorphicColors
    Scaffold(
        containerColor = neu.background,
        contentColor = neu.content,
        topBar = {
            @Suppress("NAME_SHADOWING")
            val customTopBar = topBar
            if (customTopBar != null) {
                customTopBar()
            } else if (title != null) {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = neuTopAppBarColors(),
                )
            }
        },
        floatingActionButton = floatingActionButton,
        content = content,
    )
}

/** 居中标题的页面骨架，用于 FriendCenter 这类列表页 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeuCenterAlignedScaffold(
    title: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    topBar: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
) {
    val neu = neumorphicColors
    Scaffold(
        containerColor = neu.background,
        contentColor = neu.content,
        topBar = {
            @Suppress("NAME_SHADOWING")
            val customTopBar = topBar
            if (customTopBar != null) {
                customTopBar()
            } else if (title != null) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = neuTopAppBarColors(),
                )
            }
        },
        floatingActionButton = floatingActionButton,
        content = content,
    )
}
