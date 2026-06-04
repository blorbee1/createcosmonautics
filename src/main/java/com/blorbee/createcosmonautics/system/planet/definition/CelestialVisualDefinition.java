package com.blorbee.createcosmonautics.system.planet.definition;

import net.minecraft.resources.ResourceLocation;

public record CelestialVisualDefinition(
    boolean renderBodyInSpace,
    ResourceLocation texture,
    float size,
    boolean fadeWithAltitude
) { }
