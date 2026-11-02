package io.github.aoguai.sesameag.util

import io.github.aoguai.sesameag.hook.AccountSlotRegistry
import io.github.aoguai.sesameag.hook.ApplicationHook
import io.github.aoguai.sesameag.hook.RuntimeIdentityGuard
import io.github.aoguai.sesameag.service.patch.SafeRootShell
import io.github.aoguai.sesameag.util.maps.UserMap


/**
 * 统一工作流执行权限门禁。
 *
 * `hasRoot/hasGrantedRoot` 表示“当前进程已由受支持的 libxposed 运行时注入，或实时 Root / Shizuku 可用”。
 * 即：Root 与 Shizuku 任一可用即可放行，不再强制要求 Root。
 * 实际业务执行还必须通过 `isExecutionAllowed` 检查必需权限、协议和运行账号。
 */
object WorkflowRootGuard {
    private const val TAG = "WorkflowRootGuard"
    private const val CHECK_CACHE_WINDOW_MS = 3_000L
    private val checkQueue = SerialTaskQueue()
    private val rootShell = SafeRootShell()

    @Volatile
    private var lastCheckAtMs: Long = 0L

    @Volatile
    private var lastGranted: Boolean = false

    @Volatile
    private var lastLoggedState: Boolean? = null

    fun isExecutionAllowed(): Boolean {
        if (!RuntimeIdentityGuard.isTrustedForExecution() || resolveHookAccessSource() == null) return false
        val userId = UserMap.currentUid?.trim()?.takeIf { it.isNotEmpty() } ?: return false
        return AccountSlotRegistry.isExecutableUser(userId) && CommandUtil.isExecutionAllowed(userId)
    }

    fun hasGrantedRoot(): Boolean =
        resolveHookAccessSource() != null || lastGranted || hasShizukuExecutor()

    suspend fun hasRoot(forceRefresh: Boolean = false, reason: String? = null): Boolean {
        val now = System.currentTimeMillis()
        resolveHookAccessSource()?.let { hookSource ->
            lastCheckAtMs = now
            lastGranted = true
            logState(true, reason)
            Log.record(TAG, "✅ 当前进程已完成 $hookSource 注入，允许启动工作流")
            return true
        }

        if (!forceRefresh && now - lastCheckAtMs < CHECK_CACHE_WINDOW_MS) {
            return lastGranted
        }

        return checkQueue.submit {
            val lockedNow = System.currentTimeMillis()
            resolveHookAccessSource()?.let { hookSource ->
                lastCheckAtMs = lockedNow
                lastGranted = true
                logState(true, reason)
                Log.record(TAG, "✅ 当前进程已完成 $hookSource 注入，允许启动工作流")
                return@submit true
            }
            if (!forceRefresh && lockedNow - lastCheckAtMs < CHECK_CACHE_WINDOW_MS) {
                return@submit lastGranted
            }

            val granted = try {
                resolveRootAvailability(lockedNow)
            } catch (t: Throwable) {
                Log.printStackTrace(TAG, "检测执行权限失败", t)
                false
            }

            lastCheckAtMs = lockedNow
            lastGranted = granted
            logState(granted, reason)
            granted
        }
    }

    fun invalidate() {
        lastCheckAtMs = 0L
        lastGranted = false
    }

    private suspend fun resolveRootAvailability(nowMs: Long): Boolean {
        if (ApplicationHook.classLoader != null) {
            val frameworkInfo = try {
                ApplicationHook.resolveCurrentFrameworkInfo()
            } catch (t: Throwable) {
                Log.printStackTrace(TAG, "当前进程框架识别失败", t)
                null
            }
            if (frameworkInfo != null) {
                Log.record(TAG, "🧩 当前进程框架识别: ${frameworkInfo.displayName}")
                Log.record(TAG, "✅ 检测到当前进程由 ${frameworkInfo.displayName} 注入，允许启动工作流")
                return true
            }
            Log.record(TAG, "⚠️ 当前进程框架不在 libxposed API 102 支持范围内，继续进行实时 Root/Shizuku 探测")
        } else {
            Log.record(TAG, "⚠️ 当前进程 classLoader 尚未就绪，继续进行实时 Root/Shizuku 探测")
        }

        // 先探测 Shizuku（binder ping，毫秒级），再退到 Root（可能等待 20s 超时）。
        // Root 与 Shizuku 任一可用即视为已获得执行权限。
        val hasShizuku = hasShizukuExecutor()
        if (hasShizuku) {
            Log.record(TAG, "🧪 检测到已授权的 Shizuku，跳过 Root 探测 at=$nowMs")
            return true
        }

        val hasRoot = try {
            rootShell.isAvailable()
        } catch (t: Throwable) {
            Log.printStackTrace(TAG, "实时 Root 探测失败", t)
            false
        }
        Log.record(TAG, "🧪 实时 Root/Shizuku 探测结果: root=$hasRoot shizuku=false at=$nowMs")
        return hasRoot
    }

    /**
     * Shizuku 是否已授权且服务在线。不要求以 root 身份运行（Shizuku 常见以 shell/adb 身份执行）。
     */
    private fun hasShizukuExecutor(): Boolean = try {
        val alive = rikka.shizuku.Shizuku.pingBinder()
        val granted = alive && rikka.shizuku.Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (alive && !granted) {
            Log.record(TAG, "⚠️ Shizuku 服务在线但未授权，请在 Shizuku 中授予本应用权限")
        }
        granted
    } catch (t: Throwable) {
        Log.printStackTrace(TAG, "Shizuku 状态探测失败", t)
        false
    }

    private fun resolveHookAccessSource(): String? {
        ApplicationHook.classLoader ?: return null
        val frameworkInfo = try {
            ApplicationHook.resolveCurrentFrameworkInfo()
        } catch (_: Throwable) {
            return null
        }
        return frameworkInfo.displayName.takeIf {
            ApplicationHook.hasSupportedLibXposedRuntime() &&
                isAllowedHookFramework(frameworkInfo.category)
        }
    }

    private fun isAllowedHookFramework(category: ModuleStatus.FrameworkCategory): Boolean {
        return true;
    }

    private fun logState(granted: Boolean, reason: String?) {
        if (lastLoggedState == granted) {
            return
        }
        lastLoggedState = granted

        val suffix = reason?.takeIf { it.isNotBlank() }?.let { " [$it]" }.orEmpty()
        if (granted) {
            Log.record(TAG, "✅ 已检测到可用执行权限，允许启动工作流$suffix")
        } else {
            Log.record(TAG, "⛔ 未检测到可用执行权限，工作流与配置不会生效$suffix")
        }
    }
}

