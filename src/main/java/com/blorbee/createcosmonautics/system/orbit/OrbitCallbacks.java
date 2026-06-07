package com.blorbee.createcosmonautics.system.orbit;

import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class OrbitCallbacks {
    private static final List<OrbitZoneChangeListener> LISTENERS = new ArrayList<>();

    public static void register(OrbitZoneChangeListener listener) {
        LISTENERS.add(listener);
    }

    public static void fire(Entity entity, OrbitZone from, OrbitZone to, @Nullable PlanetDefinition planet) {
        for (OrbitZoneChangeListener listener : LISTENERS) {
            listener.onZoneChanged(entity, from, to, planet);
        }
    }

    public static void fire(ServerSubLevel subLevel, OrbitZone from, OrbitZone to, @Nullable PlanetDefinition planet) {
        for (OrbitZoneChangeListener listener : LISTENERS) {
            listener.onZoneChanged(subLevel, from, to, planet);
        }
    }

    public interface OrbitZoneChangeListener {
        default void onZoneChanged(Entity entity, OrbitZone from, OrbitZone to, @Nullable PlanetDefinition planet) {}

        default void onZoneChanged(ServerSubLevel subLevel, OrbitZone from, OrbitZone to, @Nullable PlanetDefinition planet) {}
    }
}
