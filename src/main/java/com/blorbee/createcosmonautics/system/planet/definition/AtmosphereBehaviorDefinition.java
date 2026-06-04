package com.blorbee.createcosmonautics.system.planet.definition;

public record AtmosphereBehaviorDefinition(
    boolean hasOxygen,
    float baseSurfaceTemperature,
    float radiationLevel
) {}
