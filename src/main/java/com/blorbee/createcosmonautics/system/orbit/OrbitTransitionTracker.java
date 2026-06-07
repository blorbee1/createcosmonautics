package com.blorbee.createcosmonautics.system.orbit;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public final class OrbitTransitionTracker {
    private OrbitZone currentZone = OrbitZone.SURFACE;
    private OrbitZone previousZone = OrbitZone.SURFACE;
    private float transitionProgress = 0;

    @Nullable
    private ResourceLocation currentPlanetId;

    public void recalculate(double y, @Nullable PlanetDefinition planet) {
        previousZone = currentZone;

        if (planet == null) {
            currentZone = OrbitZone.DEEP_SPACE;
            transitionProgress = 1;
            currentPlanetId = null;
            return;
        }

        currentPlanetId = planet.dimensionId();
        float startY = planet.orbitTransitionStartY();
        float endY = planet.orbitTransitionEndY();

        if (y < startY) {
            currentZone = OrbitZone.SURFACE;
            transitionProgress = 0;
        } else if (y < endY) {
            currentZone = OrbitZone.TRANSITION;
            transitionProgress = (float) ((y - startY) / (endY - startY));
        } else {
            currentZone = OrbitZone.ORBIT;
            transitionProgress = 1;
        }
    }

    public OrbitZone getZone() {
        return currentZone;
    }

    public OrbitZone getPreviousZone() {
        return previousZone;
    }

    public float getTransitionProgress() {
        return transitionProgress;
    }

    public boolean zoneChangedThisTick() {
        return currentZone != previousZone;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        tag.putString("zone", currentZone.name());
        tag.putString("prevZone", previousZone.name());
        tag.putFloat("progress", transitionProgress);
        if (currentPlanetId != null) {
            tag.putString("planetId", currentPlanetId.toString());
        }
        return tag;
    }

    public static OrbitTransitionTracker deserialize(CompoundTag tag) {
        OrbitTransitionTracker tracker = new OrbitTransitionTracker();
        try {
            tracker.currentZone = OrbitZone.valueOf(tag.getString("zone"));
            tracker.previousZone = OrbitZone.valueOf(tag.getString("prevZone"));
            tracker.transitionProgress = tag.getFloat("progress");
            if (tag.contains("planetId")) {
                tracker.currentPlanetId = ResourceLocation.parse(tag.getString("planetId"));
            }
        } catch (Exception e) {
            CreateCosmonautics.LOGGER.warn("Error when deserializing OrbitTransitionTracker from tag: {}", e.toString());
        }
        return tracker;
    }

    public static OrbitTransitionTracker fromEntityTag(CompoundTag tag) {
        if (tag != null && tag.contains(CreateCosmonautics.MOD_ID + ":orbit_tracker")) {
            return deserialize(tag.getCompound(CreateCosmonautics.MOD_ID + ":orbit_tracker"));
        }
        return new OrbitTransitionTracker();
    }

    public void writeToEntityTag(CompoundTag tag) {
        if (tag == null)
            return;
        tag.put(CreateCosmonautics.MOD_ID + ":orbit_tracker", serialize());
    }
}
