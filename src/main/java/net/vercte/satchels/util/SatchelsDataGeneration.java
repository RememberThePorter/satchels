package net.vercte.satchels.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.vercte.satchels.util.assets.ItemModelGen;
import net.vercte.satchels.util.assets.LangGen;
import net.vercte.satchels.util.assets.SoundGen;
import net.vercte.satchels.util.data.CuriosProvider;
import net.vercte.satchels.util.data.ItemTagGen;
import net.vercte.satchels.util.data.StandardRecipeProvider;

import java.util.concurrent.CompletableFuture;

public class SatchelsDataGeneration {
    public static void gatherData(final GatherDataEvent event) {
        PackOutput output = event.getGenerator().getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if(event.includeClient()) {
            event.addProvider(new ItemModelGen(output, existingFileHelper));
            event.addProvider(new LangGen(output));
            event.addProvider(new SoundGen(output, existingFileHelper));
        }

        if(event.includeServer()) {
            event.addProvider(new StandardRecipeProvider(output, lookupProvider));
            event.addProvider(new ItemTagGen(output, lookupProvider));
            event.addProvider(new CuriosProvider(output, existingFileHelper, lookupProvider));
        }
    }
}
