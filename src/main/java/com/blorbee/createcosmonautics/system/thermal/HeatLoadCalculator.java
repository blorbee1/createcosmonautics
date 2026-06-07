package com.blorbee.createcosmonautics.system.thermal;

import com.blorbee.createcosmonautics.system.atmosphere.AtmosphericCondition;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3dc;

public final class HeatLoadCalculator {
    public static HeatLoad compute(Vector3dc velocity, float atmosphericDensity, ThermalContext context, PlanetDefinition planet) {
        return null;
    }

    public static HeatLoad compute(ServerSubLevel subLevel, RigidBodyHandle handle, AtmosphericCondition condition, PlanetDefinition planet) {
        return null;
    }

    public static HeatLoad compute(LivingEntity entity, AtmosphericCondition condition, PlanetDefinition planet) {
        return null;
    }

    private static HeatLoad computeReentry(Vector3dc velocity, float density) {
        return null;
    }

    private static HeatLoad computeAscent(Vector3dc velocity, float density) {
        return null;
    }

    private static HeatLoad computeAmbient(PlanetDefinition planet) {
        return null;
    }
}
