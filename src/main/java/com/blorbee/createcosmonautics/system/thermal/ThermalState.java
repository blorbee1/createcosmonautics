package com.blorbee.createcosmonautics.system.thermal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThermalState {
    private final Map<BlockPos, Float> blockTemperatures = new HashMap<>();
    private float hullAverageTemperature;

    public void applyHeatLoad(HeatLoad load, Set<BlockPos> exposedBlocks) {

    }

    public void dissipateHeat(float dt) {

    }

    public float getTemperatureKelvin(BlockPos pos) {
        return blockTemperatures.get(pos);
    }

    public float getHullAverageTemperature() {
        return hullAverageTemperature;
    }

    public boolean isOverheating(BlockPos pos) {
        return false;
    }

    public CompoundTag serialize() {
        return null;
    }

    public static ThermalState deserialize(CompoundTag tag) {
        return null;
    }
}
