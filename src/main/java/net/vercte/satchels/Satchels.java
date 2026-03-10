package net.vercte.satchels;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.vercte.satchels.compat.SatchelsCompat;
import net.vercte.satchels.network.ModPackets;
import net.vercte.satchels.util.SatchelsDataGeneration;

@Mod(Satchels.ID)
public class Satchels {
    public static final String ID = "satchels";

    public Satchels(IEventBus modEventBus) {
        ModItems.loadAndListen(modEventBus);
        ModSounds.loadAndRegister(modEventBus);

        NeoForge.EVENT_BUS.addListener(SatchelsEventHooks::playerJoin);

        modEventBus.addListener(ModPackets::registerPayloadHandlers);
        modEventBus.addListener(SatchelsDataGeneration::gatherData);

        SatchelsCompat.initialize();
    }

    public static ResourceLocation at(String path) {
        return ResourceLocation.fromNamespaceAndPath(ID, path);
    }
}
