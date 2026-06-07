package com.blorbee.createcosmonautics.system.planet.definition;

import net.minecraft.resources.ResourceLocation;

public record StarRenderDefinition(
    boolean renderStar,
    float angularSizeDegrees,
    float brightness,
    ResourceLocation starTexture
) {}
