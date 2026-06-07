package com.blorbee.createcosmonautics.system.planet.definition;

import com.blorbee.createcosmonautics.system.atmosphere.RadiationLevel;

public record AtmosphereDefinition(
    boolean hasOxygen,
    RadiationLevel radiationLevel,
    float temperatureKelvin
) {}
