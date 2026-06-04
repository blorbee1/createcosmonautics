package com.blorbee.createcosmonautics.system.planet.definition;

import net.minecraft.resources.ResourceLocation;

public record PlanetDefinition(
    ResourceLocation dimensionId,
    OrbitTransitionDefinition transition,
    CelestialVisualDefinition visuals,
    SkyBehaviorDefinition sky,
    AtmosphereBehaviorDefinition atmosphere
) {}
