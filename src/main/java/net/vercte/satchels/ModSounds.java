package net.vercte.satchels;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Satchels.ID);

    public static final Supplier<SoundEvent> SATCHEL_OPEN = SOUND_EVENTS.register("satchel_open", () -> SoundEvent.createVariableRangeEvent(Satchels.at("satchel_open")));
    public static final Supplier<SoundEvent> SATCHEL_CLOSE = SOUND_EVENTS.register("satchel_close", () -> SoundEvent.createVariableRangeEvent(Satchels.at("satchel_close")));

    public static void loadAndRegister(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
