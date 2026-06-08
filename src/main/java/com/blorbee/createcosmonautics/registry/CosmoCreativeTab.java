package com.blorbee.createcosmonautics.registry;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.simibubi.create.AllBlocks;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public final class CosmoCreativeTab {
    private static final ResourceLocation SECTION_LOCATION = CreateCosmonautics.path("cosmonautics");
    private static boolean sectionsInitialized = false;

    public static synchronized void registerAeronauticsSections() {
        if (sectionsInitialized)
            return;

        // this is a placeholder
        registerSectionItem(SECTION_LOCATION, ResourceLocation.fromNamespaceAndPath("create", "item_drain"), AllBlocks.ITEM_DRAIN::asItem);

        sectionsInitialized = true;
    }

    private static void registerSectionItem(ResourceLocation sectionId, ResourceLocation itemPath, Supplier<Item> itemSupplier) {
        SimulatedRegistrate.TAB_ITEMS.add(itemSupplier);
        SimulatedRegistrate.ITEM_TO_SECTION.put(itemPath, sectionId);
    }
}
