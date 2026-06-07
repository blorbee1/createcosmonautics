package com.blorbee.createcosmonautics.system.atmosphere;

import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.Set;

public class PressureDamageHandler {
    public static void tickSubLevel(SubLevel subLevel, AtmosphericCondition exterior) {

    }

    public static void tickEntity(LivingEntity entity, AtmosphericCondition condition) {

    }

    private static float computeInternal(SubLevel subLevel) {
        return 0;
    }

    private static void checkExposedBlocks(SubLevel subLevel, float pressureDiff) {

    }

    private static Set<BlockPos> findExposedFaces(SubLevel subLevel) {
        return Collections.emptySet();
    }
}
