package net.vercte.satchels;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vercte.satchels.satchel.SatchelItem;

import java.util.function.Supplier;

public class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Satchels.ID);

    public static final Supplier<SatchelItem> SATCHEL = ITEMS.register("satchel", SatchelItem::new);

    public static void loadAndListen(IEventBus bus) { ITEMS.register(bus); }
}
