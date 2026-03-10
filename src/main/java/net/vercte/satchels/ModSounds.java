package net.vercte.satchels;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Satchels.ID);

    public static final Supplier<SoundEvent> SATCHEL_EQUIP = dynamicRange("satchel_equip");
    public static final Supplier<SoundEvent> SATCHEL_OPEN = dynamicRange("satchel_open");
    public static final Supplier<SoundEvent> SATCHEL_CLOSE = dynamicRange("satchel_close");

    private static Supplier<SoundEvent> dynamicRange(String path) {
        return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(Satchels.at(path)));
    }

    public static void loadAndRegister(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
