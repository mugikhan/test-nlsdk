package com.example.nlscannersdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private lateinit var scannerManager: ScannerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerManager = ScannerManager(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScannerDemoScreen(scannerManager)
                }
            }
        }
    }
}

@Composable
fun ScannerDemoScreen(scannerManager: ScannerManager) {
//    var scanResult by remember { mutableStateOf("No scan yet") }
//    var isConnected by remember { mutableStateOf(false) }

    val connectionState by scannerManager.connectionState.collectAsState()
    val scanResult by scannerManager.scanResult.collectAsState()

    var displayedScanResult by remember { mutableStateOf("No scan yet") }

    LaunchedEffect(scanResult) {
        scanResult?.let {
            displayedScanResult = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "NL Scanner SDK Demo",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scanner Status",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (connectionState) "Connected" else "Disconnected",
                    color = if (connectionState) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Here you would call your SDK to connect/disconnect
                        if (connectionState) {
                            scannerManager.disconnect()
                        } else {
                            scannerManager.connect()
                        }

                    }
                ) {
                    Text(if (connectionState) "Disconnect" else "Connect")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scan Result",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

//                Text(
//                    text = scanResult,
//                    style = MaterialTheme.typography.bodyLarge
//                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Here you would trigger a scan using your SDK
                        scannerManager.triggerScan()
                    },
                    enabled = connectionState
                ) {
                    Text("Scan Barcode")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "This is a demonstration of the NL Scanner SDK",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScannerDemoPreview() {
    MaterialTheme {
        ScannerDemoScreen(scannerManager = ScannerManager(context = LocalContext.current))
    }
}