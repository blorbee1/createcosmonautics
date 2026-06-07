package com.blorbee.createcosmonautics;

import com.blorbee.createcosmonautics.config.ServerConfig;
import com.blorbee.createcosmonautics.data.CosmoDatagen;
import com.blorbee.createcosmonautics.registry.*;
import com.blorbee.createcosmonautics.system.orbit.OrbitCallbacks;
import com.blorbee.createcosmonautics.system.orbit.OrbitPhysics;
import com.blorbee.createcosmonautics.system.orbit.OrbitZone;
import com.blorbee.createcosmonautics.system.planet.definition.PlanetDefinition;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(CreateCosmonautics.MOD_ID)
public class CreateCosmonautics implements OrbitCallbacks.OrbitZoneChangeListener {
    public static final String MOD_ID = "createcosmonautics";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
        .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
        .setTooltipModifierFactory(item ->
            new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));

    public CreateCosmonautics(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.getSpec());

        CosmoPlanets.register();
        CosmoBlocks.register();
        CosmoBlockEntityTypes.register();
        CosmoCreativeTab.registerAeronauticsSections();

        modEventBus.addListener(EventPriority.HIGHEST, CosmoDatagen::gatherData);

        OrbitPhysics.init();
        OrbitCallbacks.register(this);

        LOGGER.info("Create Cosmonautics started");
    }

    public static Component lang(String path, Object... args) {
        return Component.translatable(MOD_ID + "." + path, args);
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }

    @Override
    public void onZoneChanged(Entity entity, OrbitZone from, OrbitZone to, @Nullable PlanetDefinition planet) {
        LOGGER.info("[ZONE CHANGED] entity={}\nfrom={}\nto={}\nplanet={}", entity, from, to, planet);
    }

    @Override
    public void onZoneChanged(ServerSubLevel subLevel, OrbitZone from, OrbitZone to, @Nullable PlanetDefinition planet) {
        LOGGER.info("[ZONE CHANGED] sublevel={}\nfrom={}\nto={}\nplanet={}", subLevel, from, to, planet);
    }
}
