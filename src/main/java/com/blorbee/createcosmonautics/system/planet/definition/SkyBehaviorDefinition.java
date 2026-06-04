package com.blorbee.createcosmonautics.system.planet.definition;

public record SkyBehaviorDefinition(
    boolean alwaysDarkSky,
    boolean disableStarRendering,
    float starSize
) {}
