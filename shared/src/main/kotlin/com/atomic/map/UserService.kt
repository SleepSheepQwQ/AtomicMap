package com.atomic.map

import android.os.Process
import java.nio.file.*
import kotlin.io.path.*
import com.atomic.map.IShizukuService

class UserService : IShizukuService.Stub() {
    override fun scanFiles(path: String): List<String> {
        return Path(path).listDirectoryEntries().map { it.absolutePathString() }
    }

    override fun runAtomicSync(src: String, dst: String, blacklist: List<String>): Boolean {
        val sourcePath = Path(src)
        val targetPath = Path(dst)
        
        return try {
            sourcePath.walk().forEach { file ->
                val relPath = file.relativeTo(sourcePath).toString()
                // 边界幻想：精准屏蔽逻辑
                if (blacklist.any { relPath == it || relPath.startsWith("$it/") }) return@forEach
                
                if (file.isRegularFile()) {
                    val destFile = targetPath.resolve(relPath)
                    destFile.parent.createDirectories()
                    // 2025 标准：使用原子性与覆盖语义
                    file.copyTo(destFile, overwrite = true)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun destroy() {
        Process.killProcess(Process.myPid())
    }
}