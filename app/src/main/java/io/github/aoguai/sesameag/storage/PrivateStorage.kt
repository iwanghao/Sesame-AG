package io.github.aoguai.sesameag.storage

import android.content.Context
import io.github.aoguai.sesameag.data.General
import io.github.aoguai.sesameag.storage.migration.LegacyStorageMigration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.IOException

object PrivateStorage {
    data class State(val ready: Boolean = false, val busy: Boolean = false, val message: String = "正在检查配置目录", val cleanupPending: Boolean = false,
        val needsInspection: Boolean = false)
    private val mutableState = MutableStateFlow(State())
    val state = mutableState.asStateFlow()
    private lateinit var appContext: Context
    val isReady: Boolean get() = mutableState.value.ready
    val root: File get() = File(appContext.noBackupFilesDir, "store")

    /** 用户选择“稍后处理”后写入的标记；只要存在就不再拦截启动流程。 */
    private val skipMarker: File get() = File(appContext.noBackupFilesDir, ".store-migration-skipped")
    val migrationSkipped: Boolean get() = skipMarker.isFile
    internal val context: Context get() = appContext

    @Synchronized fun initialize(context: Context) {
        check(context.packageName == General.MODULE_PACKAGE_NAME)
        appContext = context.applicationContext
        val restoreCleaned = try { StoreAccess.recoverSnapshot(appContext) } catch (failure: Exception) {
            update(State(message = "私有数据恢复未完成：${failure.message}"))
            return
        }
        if (File(root, ".store-ready").isFile) {
            update(State(ready = true, message = if (restoreCleaned) "配置已保存到应用私有目录" else "配置可用，旧恢复副本待下次启动清理",
                cleanupPending = LegacyStorageMigration.hasCleanup(context)))
        } else if (skipMarker.isFile) {
            // 用户此前选择过“稍后处理”：直接进入，不再弹迁移弹窗。
            update(State(ready = true, message = "已跳过配置迁移，使用空白配置"))
        } else {
            update(State(message = "需要检查是否有原有配置", needsInspection = true))
        }
    }

    /**
     * “稍后处理”：持久化跳过标记并直接落一个可用（可能为空）的私有存储，使本次及后续启动都不再拦截。
     * 仅更新内存状态、不落盘 .store-ready 时会在重启后重新弹窗，因此这里必须真正初始化目录。
     */
    @Synchronized fun skipMigration() {
        if (isReady) return
        check(skipMarker.parentFile?.isDirectory == true || skipMarker.parentFile?.mkdirs() == true) { "无法写入跳过标记" }
        skipMarker.writeText("skipped")

        if (!File(root, ".store-ready").isFile) {
            // 复用既有快照安装流程生成合法的空存储（含 .store-ready），避免后续 requireReady() 抛错。
            // 安装失败时不置 ready，保持内存与磁盘一致，避免后续事务写入异常磁盘状态。
            val empty = File(appContext.noBackupFilesDir, "store-initializing")
            if (empty.exists()) empty.deleteRecursively()
            check(empty.mkdirs()) { "无法创建初始化目录" }
            StoreAccess.installSnapshot(empty)
        }
        update(State(ready = true, message = "已跳过配置迁移，使用空白配置",
            cleanupPending = LegacyStorageMigration.hasCleanup(appContext)))
    }

    fun requireReady() { if (!isReady) throw IOException("请在模块应用中完成配置迁移：${mutableState.value.message}") }
    internal fun update(state: State) { mutableState.value = state }
    suspend fun inspect() = LegacyStorageMigration.inspect(appContext)
    suspend fun migrate() = LegacyStorageMigration.run(appContext)
    suspend fun backupLegacy(uri: android.net.Uri) = LegacyStorageMigration.backup(appContext, uri)
    suspend fun restoreLegacy(uri: android.net.Uri) = LegacyStorageMigration.restore(appContext, uri)
    suspend fun retryCleanup() = LegacyStorageMigration.cleanup(appContext)

    fun clearContents(): List<String> = StoreAccess.transaction {
        val failures = mutableListOf<String>()
        // The completion marker survives an in-app clear, so legacy leftovers are never imported again.
        val children = root.listFiles() ?: throw IOException("无法读取私有目录")
        children.filter { it.name != ".store-ready" }.forEach {
            if (!it.deleteRecursively()) failures.add(it.path)
        }
        StoreAccess.syncDirectory(root)
        failures
    }
}
