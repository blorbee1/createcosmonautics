package com.blorbee.createcosmonautics.system.thermal;

import com.blorbee.createcosmonautics.system.atmosphere.AtmosphericCondition;
import com.blorbee.createcosmonautics.system.orbit.IOrbitReadable;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.LivingEntity;

public class ThermalDamageHandler {
    public static void tickSubLevel(ServerSubLevel subLevel, RigidBodyHandle handle, ThermalState thermalState,
                                    AtmosphericCondition condition, PlanetDefinition planet) {

    }

    public static void tickEntity(LivingEntity entity, AtmosphericCondition condition, PlanetDefinition planet) {

    }

    private static void checkBlockDamage(SubLevel subLevel, ThermalState state) {

    }
}
