package com.blorbee.createcosmonautics.registry;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public final class CosmoCreativeTab {
    // TODO: replace with own creative tab using the section rendering like aeronautics
    private static final ResourceLocation SIMULATED_SECTION = ResourceLocation.fromNamespaceAndPath("simulated", "simulated");
    private static boolean sectionsInitialized = false;

    public static synchronized void registerAeronauticsSections() {
        if (sectionsInitialized)
            return;

//        registerSectionItem(SIMULATED_SECTION, CreateCosmonautics.path("physics_gantry_shaft"), CosmoBlocks.PHYSICS_GANTRY_SHAFT::asItem);
//        registerSectionItem(SIMULATED_SECTION, CreateCosmonautics.path("physics_gantry_carriage"), CosmoBlocks.PHYSICS_GANTRY_CARRIAGE::asItem);
//        registerSectionItem(SIMULATED_SECTION, CreateCosmonautics.path("belt_wheel"), CosmoBlocks.BELT_WHEEL::asItem);

        sectionsInitialized = true;
    }

    private static void registerSectionItem(ResourceLocation sectionId, ResourceLocation itemPath, Supplier<Item> itemSupplier) {
        SimulatedRegistrate.TAB_ITEMS.add(itemSupplier);
        SimulatedRegistrate.ITEM_TO_SECTION.put(itemPath, sectionId);
    }
}
