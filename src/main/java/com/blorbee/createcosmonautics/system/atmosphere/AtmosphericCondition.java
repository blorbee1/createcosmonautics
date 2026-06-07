package com.blorbee.createcosmonautics.system.atmosphere;

public record AtmosphericCondition(
    float oxygenPartialPressure,
    float totalPressure,
    float radiationIntensity,
    boolean isVacuum
) {
    public boolean isBreathable() {
        return true;
    }

    public float pressureDifferential(float internalPressure) {
        return Math.abs(totalPressure - internalPressure);
    }
}
