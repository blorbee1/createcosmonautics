package com.blorbee.createcosmonautics.data;

import com.blorbee.createcosmonautics.CreateCosmonautics;
import com.blorbee.createcosmonautics.ponder.CosmoPonderPlugin;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;

import static com.blorbee.createcosmonautics.CreateCosmonautics.REGISTRATE;

public class CosmoDatagen {
    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(CreateCosmonautics.MOD_ID))
            return;

        REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            JsonElement jsonElement = FilesHelper.loadJsonResource("assets/createcosmonautics/lang/default/default.json");
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                provider.add(entry.getKey(), entry.getValue().getAsString());
            }

            PonderIndex.addPlugin(new CosmoPonderPlugin());
            PonderIndex.getLangAccess().provideLang(CreateCosmonautics.MOD_ID, provider::add);
        });
    }
}
