package com.blorbee.createcosmonautics.ponder;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.blorbee.createcosmonautics.registry.CosmoBlocks;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.theme.Color;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class CosmoPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CreateCosmonautics.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderPlugin.super.registerScenes(helper);
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

//        HELPER.forComponents(CosmoBlocks.PHYSICS_GANTRY_CARRIAGE)
//            .addStoryBoard("physics_gantry/intro", PhysicsGantryScenes::introForCarriage);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderPlugin.super.registerTags(helper);
        PonderTagRegistrationHelper<RegistryEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

//        HELPER.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
//            .add(CosmoBlocks.PHYSICS_GANTRY_CARRIAGE)
//            .add(CosmoBlocks.PHYSICS_GANTRY_SHAFT);
    }

    public static void honeyGlueEffect(CreateSceneBuilder scene, Vec3 pos) {
        CreateSceneBuilder.EffectInstructions effects = scene.effects();
        effects.emitParticles(pos,
            effects.particleEmitterWithinBlockSpace(new DustParticleOptions((new Color(255, 232, 142)).asVectorF(), 1.0F),
                Vec3.ZERO), 10.0F, 2);
    }
}
