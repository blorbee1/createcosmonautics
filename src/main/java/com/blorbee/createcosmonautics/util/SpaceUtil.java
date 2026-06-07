package com.blorbee.createcosmonautics.util;

import com.blorbee.createcosmonautics.system.orbit.OrbitZone;
import com.blorbee.createcosmonautics.system.thermal.ThermalContext;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SpaceUtil {
    public static boolean isPlanetDimension(Level level) {
        return false;
    }

    @Nullable
    public static SubLevel getSubLevelAt(Level level, BlockPos pos) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null)
            return null;
        return container.getSubLevel(pos.getX(), pos.getZ());
    }

    public static boolean isSpaceCapable(ServerSubLevel subLevel) {
        return false;
    }

    public static Vector3d getAbsoluteVelocity(RigidBodyHandle handle) {
        return new Vector3d();
    }

    public static ThermalContext classifyThermalContext(Vector3dc velocity, OrbitZone zone, float atmosphericDensity) {
        return null;
    }
}
