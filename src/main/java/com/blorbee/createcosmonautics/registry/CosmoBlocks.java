package com.blorbee.createcosmonautics.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.*;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static com.blorbee.createcosmonautics.CreateCosmonautics.REGISTRATE;

public final class CosmoBlocks {
//    public static final BlockEntry<PhysicsGantryShaftBlock> PHYSICS_GANTRY_SHAFT =
//        REGISTRATE.block("physics_gantry_shaft", PhysicsGantryShaftBlock::new)
//            .initialProperties(SharedProperties::stone)
//            .properties(p -> p
//                .mapColor(MapColor.NETHER)
//                .forceSolidOn()
//            )
//            .transform(TagGen.axeOrPickaxe())
//            .blockstate((c, p) -> p.directionalBlock(c.get(), s -> {
//                boolean isPowered = s.getValue(PhysicsGantryShaftBlock.POWERED);
//                boolean isFlipped = s.getValue(PhysicsGantryShaftBlock.FACING)
//                    .getAxisDirection() == Direction.AxisDirection.NEGATIVE;
//                String partName = s.getValue(PhysicsGantryShaftBlock.PART)
//                    .getSerializedName();
//                String flipped = isFlipped ? "_flipped" : "";
//                String powered = isPowered ? "_powered": "";
//
//                ModelFile existing = p.models().getExistingFile(p.modLoc("block/gantry_shaft/block_" + partName));
//                if (!isPowered && !isFlipped)
//                    return existing;
//
//                return p.models()
//                    .withExistingParent("block/gantry_shaft_" + partName + powered + flipped, existing.getLocation())
//                    .texture("2", p.modLoc("block/gantry/gantry_shaft" + powered + flipped));
//            }))
//            .lang("Physics Gantry Shaft")
//            .recipe((ctx, provider) -> {
//                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get(), 8)
//                    .pattern("B")
//                    .pattern("R")
//                    .pattern("B")
//                    .define('B', AllItems.BRASS_INGOT)
//                    .define('R', Items.REDSTONE)
//                    .unlockedBy("has_brass", RegistrateRecipeProvider.has(AllItems.BRASS_INGOT))
//                    .unlockedBy("has_redstone", RegistrateRecipeProvider.has(Items.REDSTONE))
//                    .save(provider);
//            })
//            .item()
//            .model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/gantry_shaft/block_single")))
//            .build()
//            .register();

    public static void register() {}
}
