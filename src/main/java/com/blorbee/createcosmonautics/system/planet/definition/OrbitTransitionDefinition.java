package com.blorbee.createcosmonautics.system.planet.definition;

public record OrbitTransitionDefinition(
    int startSpaceY,
    int fullSpaceY,
    boolean enableOrbitTransition
) { }
