package com.blorbee.createcosmonautics.registry;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.blorbee.createcosmonautics.system.atmosphere.RadiationLevel;
import com.blorbee.createcosmonautics.system.planet.PlanetRegistry;
import com.blorbee.createcosmonautics.system.planet.definition.*;
import net.minecraft.resources.ResourceLocation;

public class CosmoPlanets {
    public static final ResourceLocation EARTH_ID = CreateCosmonautics.path("earth");

    public static final PlanetDefinition EARTH = new PlanetDefinition(
        ResourceLocation.withDefaultNamespace("overworld"),
        new StarRenderDefinition(
            true,
            0.5f,
            1.0f,
            ResourceLocation.withDefaultNamespace("textures/enviroment/sun.png")
        ),
        new AtmosphereDefinition(
            true,
            RadiationLevel.NONE,
            288f
        ),
        true,
        700f,
        1000f
    );

    public static void register() {
        PlanetRegistry.register(EARTH_ID, EARTH);
    }
}
