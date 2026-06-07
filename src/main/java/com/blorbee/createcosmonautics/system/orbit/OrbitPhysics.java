package com.blorbee.createcosmonautics.system.orbit;

import com.blorbee.createcosmonautics.system.planet.PlanetRegistry;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Comparator;
import java.util.ServiceLoader;

public final class OrbitPhysics {
    public static void init() {
        SableEventPlatform.INSTANCE.onPhysicsTick(((physicsSystem, dt) -> {
            ServerLevel level = physicsSystem.getLevel();
            SubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null)
                return;

            PlanetDefinition planet = PlanetRegistry.forLevel(level).orElse(null);
            for (SubLevel subLevel : container.getAllSubLevels()) {
                if (!(subLevel instanceof ServerSubLevel serverSubLevel))
                    continue;

                RigidBodyHandle handle = physicsSystem.getPhysicsHandle(serverSubLevel);
                if (handle == null)
                    continue;

                processSubLevel(serverSubLevel, handle, level, planet, dt);
            }
        }));
    }

    private static void processSubLevel(ServerSubLevel subLevel, RigidBodyHandle handle, ServerLevel level,
                                        PlanetDefinition planet, double dt) {
        if (planet != null && !planet.hasOrbitTransition())
            return;

        CompoundTag data = subLevel.getUserDataTag();
        if (data == null) {
            data = new CompoundTag();
            subLevel.setUserDataTag(data);
        }

        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        double altitude = subLevel.logicalPose().position().y;
        tracker.recalculate(altitude, planet);

        OrbitZone zone = tracker.getZone();
        if (zone.isAboveSurface()) {
            applyCounterforce(handle, subLevel, level, tracker.getTransitionProgress(), zone, dt);
        }

        tracker.writeToEntityTag(data);
        if (tracker.zoneChangedThisTick()) {
            OrbitCallbacks.fire(subLevel, tracker.getPreviousZone(), zone, planet);
        }
    }

    private static void applyCounterforce(RigidBodyHandle handle, ServerSubLevel subLevel, ServerLevel level,
                                          float progress, OrbitZone zone, double dt) {
        Vector3dc worldGravity = DimensionPhysicsData.getGravity(level);
        double mass = subLevel.getMassTracker().getMass();

        float effectiveProgress = zone.isVacuum() ? 1.0f : progress;
        Vector3d worldCounterForce = new Vector3d(
            -worldGravity.x() * mass * dt * effectiveProgress,
            -worldGravity.y() * mass * dt * effectiveProgress,
            -worldGravity.z() * mass * dt * effectiveProgress
        );

        Quaterniond rotation = subLevel.logicalPose().orientation();
        Vector3d localCounterForce = rotation.transformInverse(worldCounterForce);

        handle.applyLinearImpulse(localCounterForce);
    }
}
