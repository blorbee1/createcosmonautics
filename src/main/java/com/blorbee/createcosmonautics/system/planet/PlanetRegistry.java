package com.blorbee.createcosmonautics.system.planet;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class PlanetRegistry {
    private static final Map<ResourceLocation, Planet> PLANETS = new HashMap<>();

    public static Planet register(Planet planet) throws IllegalArgumentException {
        if (PLANETS.containsKey(planet.id()))
            throw new IllegalArgumentException("Planet with id " + planet.id() + " already exists");
        PLANETS.put(planet.id(), planet);
        return planet;
    }
}
