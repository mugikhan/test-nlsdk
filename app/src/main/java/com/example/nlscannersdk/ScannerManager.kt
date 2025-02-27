package com.example.nlscannersdk

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.util.Log
import com.nlscan.nlsdk.NLDevice
import com.nlscan.nlsdk.NLDeviceStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScannerManager(private val context: Context) {
    private val TAG = "ScannerManager"
    private val nlDevice = NLDevice(NLDeviceStream.DevClass.DEV_COMPOSITE)

    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState.asStateFlow()

    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult.asStateFlow()

    fun connect() {
        try {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val permissionIntent = PendingIntent.getBroadcast(
                context, 0, Intent("com.example.nlscannersdk.USB_PERMISSION"),
                PendingIntent.FLAG_IMMUTABLE
            )

            val success = nlDevice.nl_OpenDevice(context, object : NLDeviceStream.NLUsbListener {
                override fun actionUsbPlug(event: Int) {
                    if (event == 0) { // Unplugged
                        _connectionState.value = false
                        Log.d(TAG, "USB device unplugged")
                    } else { // Plugged in
                        Log.d(TAG, "USB device plugged in")
                    }
                }

                override fun actionUsbRecv(data: ByteArray, length: Int) {
                    val barcode = String(data, 0, length)
                    _scanResult.value = barcode
                    Log.d(TAG, "Received barcode: $barcode")
                }
            })

            _connectionState.value = success
            Log.d(TAG, "Connection attempt result: $success")
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to scanner", e)
            _connectionState.value = false
        }
    }

    fun disconnect() {
        try {
            nlDevice.nl_CloseDevice()
            _connectionState.value = false
            Log.d(TAG, "Disconnected from scanner")
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting from scanner", e)
        }
    }

    fun triggerScan() {
        // If your scanner supports software triggering, implement it here
        Log.d(TAG, "Trigger scan requested")
    }
}