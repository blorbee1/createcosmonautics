package com.blorbee.createcosmonautics.system.atmosphere;

public enum RadiationLevel {
    NONE,
    LOW,
    MODERATE,
    HIGH,
    LETHAL;

    public float damagePerSecond() {
        return 0;
    }
}
