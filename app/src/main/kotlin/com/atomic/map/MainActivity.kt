package com.atomic.map

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var src by remember { mutableStateOf("/sdcard/Download") }
            var dst by remember { mutableStateOf("/sdcard/AtomicBackup") }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text("AtomicMap High-Precision Sync", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = src, onValueChange = { src = it }, label = { Text("Source") })
                TextField(value = dst, onValueChange = { dst = it }, label = { Text("Target") })
                Button(onClick = { /* TODO: Bind Shizuku & Sync */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Execute Atomic Mapping")
                }
            }
        }
    }
}