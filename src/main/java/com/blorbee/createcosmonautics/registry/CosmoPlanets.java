package com.blorbee.createcosmonautics.registry;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.blorbee.createcosmonautics.system.atmosphere.RadiationLevel;
import com.blorbee.createcosmonautics.system.planet.PlanetRegistry;
import com.blorbee.createcosmonautics.system.planet.definition.*;
import net.minecraft.resources.ResourceLocation;

public class CosmoPlanets {
    public static final ResourceLocation EARTH_ID = CreateCosmonautics.path("earth");

    public static final PlanetDefinition EARTH = PlanetDefinition.builder(ResourceLocation.withDefaultNamespace("overworld"))
        .starSystem(CosmoStarSystems.SOL)
        .build();

    public static void register() {
        PlanetRegistry.register(EARTH_ID, EARTH);
    }
}
