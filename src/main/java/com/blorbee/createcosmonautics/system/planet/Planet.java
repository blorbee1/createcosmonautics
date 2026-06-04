package com.blorbee.createcosmonautics.system.planet;

import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import net.minecraft.resources.ResourceLocation;

public final class Planet {
    private final ResourceLocation id;
    private final PlanetDefinition definition;

    public Planet(ResourceLocation id, PlanetDefinition definition) {
        this.id = id;
        this.definition = definition;
    }

    public ResourceLocation id() {
        return id;
    }
    public PlanetDefinition definition() {
        return definition;
    }
}
