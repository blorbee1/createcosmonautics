package com.blorbee.createcosmonautics.system.planet;

import com.blorbee.createcosmonautics.system.planet.definition.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

public final class PlanetRegistry {
    private static final Map<ResourceLocation, PlanetDefinition> PLANETS = new HashMap<>();

    public static void register(ResourceLocation id, PlanetDefinition def) throws IllegalStateException {
        if (PLANETS.containsKey(id))
            throw new IllegalStateException("Planet already registered: " + id);
        PLANETS.put(id, def);
    }

    public static Optional<PlanetDefinition> forLevel(Level level) {
        ResourceLocation dimId = level.dimension().location();
        return PLANETS.values().stream()
            .filter(p -> p.dimensionId().equals(dimId))
            .findFirst();
    }

    public static Optional<PlanetDefinition> get(ResourceLocation id) {
        return Optional.ofNullable(PLANETS.get(id));
    }

    public static Collection<PlanetDefinition> all() {
        return Collections.unmodifiableCollection(PLANETS.values());
    }

    public static void validate() {
        for (Map.Entry<ResourceLocation, PlanetDefinition> entry : PLANETS.entrySet()) {
            // TODO:
        }
    }
}
