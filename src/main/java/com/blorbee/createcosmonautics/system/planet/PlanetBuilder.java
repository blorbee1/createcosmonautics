package com.blorbee.createcosmonautics.system.planet;

import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import net.minecraft.resources.ResourceLocation;

public class PlanetBuilder {
    private final ResourceLocation id;
    private PlanetDefinition definition;

    public PlanetBuilder(ResourceLocation id) {
        this.id = id;
    }

    public static PlanetBuilder of(ResourceLocation id, PlanetDefinition definition) {
        return new PlanetBuilder(id).definition(definition);
    }

    public PlanetBuilder definition(PlanetDefinition definition) {
        this.definition = definition;
        return this;
    }

    public Planet build() throws IllegalStateException {
        if (definition == null)
            throw new IllegalStateException("PlanetDefinition cannot be null when building");
        return new Planet(id, definition);
    }
}
