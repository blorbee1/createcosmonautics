package com.blorbee.createcosmonautics.system.atmosphere;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class AtmosphereManager {
    float getPressure(Level level, Vec3 pos) {}

    boolean isVacuum(Level level, Vec3 pos) {}

    boolean hasBreathableAir(Level level, Vec3 pos) {}
}
