package com.blorbee.createcosmonautics.system.planet.definition;

public record OrbitDefinition(
   float orbitRadiusAU,
   float orbitPeriodDays,
   float orbitEccentricity,
   float currentAngleDeg
) {
    public static OrbitDefinition earth() {
        return new OrbitDefinition(1.0f, 365f, 0.017f, 0f);
    }

    public static OrbitDefinition moon() {
        return new OrbitDefinition(0.0026f, 27f, 0.055f, 0f);
    }
}
