package com.blorbee.createcosmonautics.system.atmosphere;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface AtmosphericConditionProvider {
    AtmosphericCondition sample(Level level, double altitude);

    AtmosphericCondition sampleAtEntity(Entity entity);
}
