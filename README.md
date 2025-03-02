# NLScannerSDK

A library for integrating NLScan barcode scanners with Android and Flutter applications.

[![](https://jitpack.io/v/mugikhan/test-nlsdk.svg)](https://jitpack.io/#mugikhan/test-nlsdk)

## Features

- Easy USB connection management for NLScan devices
- Barcode scanning and data processing
- Support for various NLScan scanner models
- Configurable scanner settings

## Installation

### Android

1. Add JitPack repository to your root `build.gradle` or `settings.gradle` file:

```groovy
// For build.gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

// OR for settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        ...
        maven { url = uri("https://jitpack.io") }
    }
}
```

2. Add the dependency to your app's `build.gradle` file:

```groovy
dependencies {
    implementation 'com.github.mugikhan.test-nlsdk:nlsdk:1.0.0'
}
```

### Flutter

1. Add the dependency to your `pubspec.yaml` file:

```yaml
dependencies:
  nlscannersdk:
    git:
      url: https://github.com/mugikhan/test-nlsdk.git
      ref: main # or specify a tag/commit
```

2. For Flutter, you'll need to create platform channels to communicate with the native SDK. See the usage example below.

## Usage

### Android

```kotlin
import com.nlscan.nlsdk.NLDevice
import com.nlscan.nlsdk.NLDeviceStream

class YourActivity : AppCompatActivity() {
    private lateinit var scannerManager: ScannerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize scanner manager
        scannerManager = ScannerManager(this)

        // Set up listeners
        lifecycleScope.launch {
            scannerManager.connectionState.collect { isConnected ->
                // Update UI based on connection state
                updateConnectionUI(isConnected)
            }
        }

        lifecycleScope.launch {
            scannerManager.scanResult.collect { result ->
                result?.let {
                    // Process scan result
                    processScanResult(it)
                }
            }
        }

        // Connect to scanner
        scannerManager.connectToScanner()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerManager.disconnectScanner()
    }
}
```

### Flutter

1. Create a method channel in your Flutter app:

```dart
import 'package:flutter/services.dart';

class NLScannerPlugin {
  static const MethodChannel _channel = MethodChannel('com.example.nlscannersdk/scanner');

  static Future<bool> connectScanner() async {
    try {
      return await _channel.invokeMethod('connectScanner');
    } catch (e) {
      print('Error connecting scanner: $e');
      return false;
    }
  }

  static Future<void> disconnectScanner() async {
    try {
      await _channel.invokeMethod('disconnectScanner');
    } catch (e) {
      print('Error disconnecting scanner: $e');
    }
  }

  // Set up event channel for scan results
  static Stream<String> get scanResults {
    const EventChannel eventChannel = EventChannel('com.example.nlscannersdk/scanner_events');
    return eventChannel.receiveBroadcastStream().map((event) => event.toString());
  }
}
```

2. Use the plugin in your Flutter app:

```dart
import 'package:flutter/material.dart';
import 'package:your_app/nl_scanner_plugin.dart';

class ScannerScreen extends StatefulWidget {
  @override
  _ScannerScreenState createState() => _ScannerScreenState();
}

class _ScannerScreenState extends State<ScannerScreen> {
  String _lastScan = '';
  bool _isConnected = false;

  @override
  void initState() {
    super.initState();
    _connectScanner();
    _listenForScans();
  }

  Future<void> _connectScanner() async {
    final connected = await NLScannerPlugin.connectScanner();
    setState(() {
      _isConnected = connected;
    });
  }

  void _listenForScans() {
    NLScannerPlugin.scanResults.listen((scan) {
      setState(() {
        _lastScan = scan;
      });
    });
  }

  @override
  void dispose() {
    NLScannerPlugin.disconnectScanner();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Scanner Demo')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Scanner connected: ${_isConnected ? 'Yes' : 'No'}'),
            SizedBox(height: 20),
            Text('Last scan: $_lastScan'),
          ],
        ),
      ),
    );
  }
}
```

## Permissions

For Android, add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-feature android:name="android.hardware.usb.host" />
<uses-permission android:name="android.permission.USB_PERMISSION" />
```

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
