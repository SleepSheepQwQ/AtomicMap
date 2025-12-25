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
    [span_10](start_span)[span_11](start_span)// 提取为成员变量，确保 onDestroy 可访问[span_10](end_span)[span_11](end_span)
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
                Text("AtomicMap High-Precision Sync", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = src, onValueChange = { src = it }, label = { Text("Source Path") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = dst, onValueChange = { dst = it }, label = { Text("Target Path") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (syncService == null) {
                            bindShizukuService()
                            Toast.makeText(this@MainActivity, "Binding Shizuku...", Toast.LENGTH_SHORT).show()
                        } else {
                            isRunning = true
                            Thread {
                                [span_12](start_span)// 原子同步调用逻辑[span_12](end_span)
                                val success = try { syncService?.runAtomicSync(src, dst, emptyList()) ?: false } catch (e: Exception) { false }
                                isRunning = false
                                runOnUiThread { Toast.makeText(this@MainActivity, if(success) "Sync Complete" else "Sync Failed", Toast.LENGTH_LONG).show() }
                            }.start()
                        }
                    }, 
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRunning
                ) {
                    Text(if (isRunning) "Syncing..." else "Execute Atomic Mapping")
                }
            }
        }
    }

    private fun bindShizukuService() {
        if (Shizuku.pingBinder()) {
            val args = ShizukuRemoteServiceArgs(userServiceComponent)
                .daemon(false)
                .processNameSuffix("service")
                .debuggable(true)
            Shizuku.bindUserService(args, connection)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(binderListener)
        if (Shizuku.pingBinder()) {
            try { 
                [span_13](start_span)// 修复：使用成员变量重新构建解绑参数[span_13](end_span)
                val args = ShizukuRemoteServiceArgs(userServiceComponent)
                Shizuku.unbindUserService(args, connection, true) 
            } catch (e: Exception) {}
        }
    }
}
