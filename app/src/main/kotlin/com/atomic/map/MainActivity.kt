package com.atomic.map

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rikka.shizuku.Shizuku
import dev.rikka.shizuku.ShizukuRemoteServiceArgs

class MainActivity : ComponentActivity() {
    private var syncService: IShizukuService? = null
    // 修复点 1：定义为成员变量，全类可见
    private val userServiceComponent = ComponentName("com.atomic.map", "com.atomic.map.UserService")

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            syncService = IShizukuService.Stub.asInterface(binder)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            syncService = null
        }
    }

    private val binderListener = Shizuku.OnBinderReceivedListener {
        bindShizukuService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shizuku.addBinderReceivedListener(binderListener)
        
        setContent {
            var src by remember { mutableStateOf("/sdcard/Download") }
            var dst by remember { mutableStateOf("/sdcard/AtomicBackup") }
            var isRunning by remember { mutableStateOf(false) }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AtomicMap Sync", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = src, onValueChange = { src = it }, label = { Text("源路径") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = dst, onValueChange = { dst = it }, label = { Text("目标路径") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (syncService == null) {
                            bindShizukuService()
                        } else {
                            isRunning = true
                            Thread {
                                try { syncService?.runAtomicSync(src, dst, emptyList()) } catch (e: Exception) {}
                                isRunning = false
                            }.start()
                        }
                    }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRunning
                ) {
                    Text(if (isRunning) "同步中..." else "开始同步")
                }
            }
        }
    }

    private fun bindShizukuService() {
        if (Shizuku.pingBinder()) {
            val args = ShizukuRemoteServiceArgs(userServiceComponent)
                .daemon(false).processNameSuffix("service").debuggable(true)
            Shizuku.bindUserService(args, connection)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderListener)
        if (Shizuku.pingBinder()) {
            try { 
                // 修复点 2：使用成员变量重新创建 args，确保解绑逻辑闭环
                val args = ShizukuRemoteServiceArgs(userServiceComponent)
                Shizuku.unbindUserService(args, connection, true) 
            } catch (e: Exception) {}
        }
    }
}
