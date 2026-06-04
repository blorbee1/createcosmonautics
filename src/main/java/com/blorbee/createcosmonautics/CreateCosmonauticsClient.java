package com.blorbee.createcosmonautics;

import com.blorbee.createcosmonautics.ponder.CosmoPonderPlugin;
import com.blorbee.createcosmonautics.registry.CosmoPartialModels;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CreateCosmonautics.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateCosmonautics.MOD_ID, value = Dist.CLIENT)
public class CreateCosmonauticsClient {
    public CreateCosmonauticsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        CreateCosmonautics.LOGGER.info("Create Cosmonautics client started");
        event.enqueueWork(CreateCosmonauticsClient::clientInit);
    }

    public static void clientInit() {
        CosmoPartialModels.register();
        PonderIndex.addPlugin(new CosmoPonderPlugin());
    }
}
