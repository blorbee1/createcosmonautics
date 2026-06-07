package com.blorbee.createcosmonautics.registry;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.blorbee.createcosmonautics.system.planet.StarSystem;

public class CosmoStarSystems {
    public static final StarSystem SOL = new StarSystem(
        CreateCosmonautics.path("sol"),
        5778f,
        1.0f
    );

    public static void register() {}
}
