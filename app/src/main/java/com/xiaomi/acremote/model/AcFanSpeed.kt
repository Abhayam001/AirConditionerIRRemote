package com.xiaomi.acremote.model

enum class AcFanSpeed(val displayName: String, val level: Int) {
    AUTO("Auto", 0),
    LOW("Low", 1),
    MEDIUM("Med", 2),
    HIGH("High", 3),
    TURBO("Turbo", 4)
}
