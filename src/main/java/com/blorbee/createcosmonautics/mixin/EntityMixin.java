package com.blorbee.createcosmonautics.mixin;

import com.blorbee.createcosmonautics.system.orbit.OrbitCallbacks;
import com.blorbee.createcosmonautics.system.orbit.OrbitTransitionTracker;
import com.blorbee.createcosmonautics.system.orbit.OrbitZone;
import com.blorbee.createcosmonautics.system.planet.PlanetRegistry;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    private static final double DEFAULT_GRAVITY = 11;

    @Shadow
    public abstract CompoundTag getPersistentData();

    @Shadow
    public abstract double getY();

    @Shadow
    private Level level;

    @ModifyExpressionValue(method = "getGravity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDefaultGravity()D"))
    private double cosmonautics$applyGravityCounterforce(double original) {
        PlanetDefinition planet = PlanetRegistry.forLevel(level).orElse(null);
        if (planet != null && !planet.hasOrbitTransition())
            return original;

        original *= Math.abs(DimensionPhysicsData.getGravity(level).y) / DEFAULT_GRAVITY; // entity class always negates the output of this method

        CompoundTag data = getPersistentData();
        OrbitTransitionTracker tracker = OrbitTransitionTracker.fromEntityTag(data);

        tracker.recalculate(getY(), planet);

        OrbitZone zone = tracker.getZone();
        if (zone.isAboveSurface()) {
            original *= 1.0 - tracker.getTransitionProgress();
        }

        tracker.writeToEntityTag(data);
        if (tracker.zoneChangedThisTick()) {
            OrbitCallbacks.fire((Entity) (Object) this, tracker.getPreviousZone(), zone, planet);
        }

        return original;
    }
}
