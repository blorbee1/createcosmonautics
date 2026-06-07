package com.blorbee.createcosmonautics.system.planet.definition;

import net.minecraft.resources.ResourceLocation;

public record PlanetDefinition(
    ResourceLocation dimensionId,
    StarRenderDefinition starRender,
    AtmosphereDefinition atmosphere,
    boolean hasOrbitTransition,
    float orbitTransitionStartY,
    float orbitTransitionEndY
) {}
