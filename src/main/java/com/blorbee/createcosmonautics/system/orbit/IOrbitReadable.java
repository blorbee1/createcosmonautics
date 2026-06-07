package com.blorbee.createcosmonautics.system.orbit;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IOrbitReadable {
    /**
     * Get the current orbit zone the player is in
     * @param player Player to lookup
     * @return Current orbit zone
     */
    OrbitZone getZone(@NotNull ServerPlayer player);

    /**
     * Returns 0..1 percentage where 0 is surface 1 is full orbit
     * @param player Player to lookup
     */
    float getTransitionProgress(@NotNull ServerPlayer player);

    /**
     * Get the current planet the player is on
     * @param player Player to lookup
     * @return Current planet or no planet
     */
    @Nullable
    ResourceLocation getCurrentPlanet(@NotNull ServerPlayer player);
}
