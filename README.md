# Mi AC Remote - Universal Air Conditioner Controller for Xiaomi

A modern Android application designed for **Xiaomi smartphones equipped with an Infrared (IR) Blaster** (Mi, Redmi, POCO series) to control any Air Conditioner (AC) in the world.

---

## 🌟 Key Features

1. **Native Xiaomi Hardware IR Transmission**:
   - Uses Android's native `android.hardware.ConsumerIrManager` service.
   - Operates on precise carrier frequencies (standard 38,000 Hz / 38 kHz).
   - Generates exact microsecond pulse train arrays `[Mark1, Space1, Mark2, Space2, ...]`.

2. **Stateful Air Conditioner Protocol Encoders**:
   Unlike simple TV remotes that send a single keycode, AC remotes are **stateful** (every button press sends the full state: Power, Mode, Temperature, Fan Speed, Swing, Turbo, Eco, and CRC/Checksum).
   Supported brand protocol engines include:
   - **Gree**: (Used by Gree, GE, Tosot, Kinghome, Lennox, Voltas OEM, Blue Star OEM)
   - **Midea**: (Used by Midea, Carrier, Pioneer, Senville, Danby, Comfee, Toshiba, Godrej OEM)
   - **LG**: 28-bit LG Dual Inverter protocol with checksum
   - **Daikin**: ARC433 series dual-frame protocol with 8-byte preamble & 19-byte command
   - **Samsung**: 14-byte extended protocol with inverted checksum
   - **Panasonic**: 27-byte dual-frame protocol (Econavi / Inverter series)
   - **Mitsubishi Electric**: 18-byte MSZ / Mr Slim series
   - **AUX**: 13-byte protocol (AUX, York, Daewoo OEM, Hisense OEM)
   - **TCL**: 14-byte Elite / Miracle series
   - **Haier**: 14-byte Smart Cool series

3. **Universal AC Auto-Match / Quick Pairing**:
   - Have an unbranded or unknown AC model?
   - Tap the **Auto-Match** button: the app steps through test power bursts across candidate protocols.
   - If your AC beeps or responds, tap **"Yes, Save!"** to lock in your brand immediately!

4. **Tactile & Modern UI (Jetpack Compose + Material 3)**:
   - **Interactive Temperature Dial**: Large bold display with increment/decrement steppers and 18°/22°/24°/26° quick chips.
   - **Dynamic Glow Lighting**: Responsive glow shifts from icy cyan in Cool mode to warm orange in Heat mode.
   - **Operation Modes**: Cool, Heat, Auto, Dry, Fan.
   - **Blower Speed**: Auto, Low, Medium, High, Turbo with visual level bars.
   - **Power Button**: Large glowing circular tactile toggle.
   - **Special Functions**: 3D Swing, Turbo Boost, Eco Energy Saver, Sleep, and Indoor Unit Display Light.
   - **Haptic Vibration**: Gentle tactile clicks on every IR pulse transmission.

5. **Diagnostic IR Signal Inspector & Simulation Mode**:
   - Real-time pulse waveform visualizer.
   - Logs timestamp, carrier frequency, pulse count, duration in milliseconds, and microsecond pulse sample.
   - If tested on a non-IR device or emulator, gracefully enters simulation mode without crashing.

---

## 📱 Hardware Compatibility

Fully compatible with all Xiaomi / Redmi / POCO devices equipped with an IR Blaster:
- **Xiaomi Series**: Xiaomi 15 / 14 / 13 / 12 / 11 / 10 / 9 / 8 / 6 / 5
- **Redmi Note Series**: Redmi Note 13 / 12 / 11 / 10 / 9 / 8 / 7 / 6 / 5 / 4
- **POCO Series**: POCO F6 / F5 / F4 / X6 / X5 / X4 / M6 / M5 / M4
- **Redmi K Series**: Redmi K70 / K60 / K50 / K40 / K30 / K20
- Any other Android phone featuring a built-in Consumer IR emitter (e.g. Huawei, Honor, Vivo, Oppo models with IR).

---

## 🛠️ Project Structure

```
AirConditionerIRRemote/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml          # Transmit IR & Vibrate permissions
│   │   ├── java/com/xiaomi/acremote/
│   │   │   ├── MainActivity.kt          # Edge-to-edge Compose entry point
│   │   │   ├── AcRemoteApplication.kt
│   │   │   ├── ir/
│   │   │   │   ├── IrTransmitter.kt     # ConsumerIrManager wrapper & haptics
│   │   │   │   ├── IrPulseBuilder.kt    # Mark/Space microsecond timing builder
│   │   │   │   └── IrSignalLogger.kt    # Real-time packet telemetry
│   │   │   ├── model/
│   │   │   │   ├── AcBrand.kt           # Supported brands & OEM aliases
│   │   │   │   ├── AcState.kt           # Full AC operational state
│   │   │   │   ├── AcMode.kt            # Cool, Heat, Auto, Dry, Fan
│   │   │   │   ├── AcFanSpeed.kt        # Auto, Low, Med, High, Turbo
│   │   │   │   └── AcSwing.kt           # Off, Vertical, Horizontal, Both
│   │   │   ├── protocol/
│   │   │   │   ├── AcProtocol.kt        # Interface for IR encoders
│   │   │   │   ├── GreeProtocol.kt      # Gree, Tosot, GE, Lennox, Voltas OEM
│   │   │   │   ├── MideaProtocol.kt     # Midea, Carrier, Pioneer, Toshiba OEM
│   │   │   │   ├── LgProtocol.kt        # LG Dual Inverter
│   │   │   │   ├── DaikinProtocol.kt    # Daikin ARC433 series
│   │   │   │   ├── SamsungProtocol.kt   # Samsung WindFree / Inverter
│   │   │   │   ├── PanasonicProtocol.kt # Panasonic Inverter / Econavi
│   │   │   │   ├── MitsubishiProtocol.kt# Mitsubishi Electric Mr Slim
│   │   │   │   ├── AuxProtocol.kt       # AUX & OEM
│   │   │   │   ├── TclProtocol.kt       # TCL Elite
│   │   │   │   ├── HaierProtocol.kt     # Haier Smart Cool
│   │   │   │   └── ProtocolRegistry.kt  # Lookup & Auto-Match scan list
│   │   │   ├── ui/
│   │   │   │   ├── components/          # TemperatureWheel, Mode, Fan, Power, QuickActions
│   │   │   │   ├── screens/             # MainRemoteScreen, BrandPickerSheet, QuickPairingDialog
│   │   │   │   ├── theme/               # Dark sci-fi theme & typography
│   │   │   │   └── viewmodel/           # RemoteViewModel state engine
│   │   └── res/                         # Icons, strings, styles
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml               # Modern Gradle Version Catalog
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 How to Build and Run

### Option 1: Using Android Studio (Recommended)
1. Open **Android Studio** (Hedgehog, Iguana, Jellyfish, Koala, Ladybug or newer).
2. Click **Open** and select the folder:
   `/Users/abhyudaylakhani/Downloads/AirConditionerIRRemote`
3. Connect your Xiaomi phone via USB with **USB Debugging** enabled in Developer Options.
4. Click the green **Run** ▶️ button (or press `Shift + F10`).

### Option 2: Command Line (Gradle)
```bash
cd /Users/abhyudaylakhani/Downloads/AirConditionerIRRemote
./gradlew assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

Install directly onto your Xiaomi phone via `adb`:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```
