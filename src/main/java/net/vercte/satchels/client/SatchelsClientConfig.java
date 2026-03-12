package net.vercte.satchels.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.PacketDistributor;
import net.vercte.satchels.network.packets.SatchelOffsetUpdatePacketC2S;
import net.vercte.satchels.satchel.SatchelData;

public class SatchelsClientConfig {
    private static int satchelOffset = 0;

    public static int getSatchelOffset() {
        return satchelOffset;
    }

    // region Spec
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue SATCHEL_OFFSET = BUILDER
            .comment("The offset in the position of your satchel on your hotbar. 0 = covers slots 1-6, 3 = covers slots 4-9")
            .defineInRange("satchel_offset", 0, 0, 3);

    static final ModConfigSpec SPEC = BUILDER.build();
    // endregion

    // region Controls Hook
    private static final OptionInstance<Integer> offsetOption = new OptionInstance<>(
            "satchels.options.offset",
            OptionInstance.cachedConstantTooltip(Component.translatable("satchels.options.offset.tooltip")),
            (c, v) -> Component.translatable("satchels.options.offset.selection", v+1, v+6),
            new OptionInstance.ClampingLazyMaxIntRange(0, () -> 3, 3),
            0,
            v -> {
                SATCHEL_OFFSET.set(v);
                SATCHEL_OFFSET.save();
            }
    );

    public static OptionInstance<Integer> getOffsetOption() {
        return offsetOption;
    }
    // endregion

    private static void onConfigUpdate(final ModConfigEvent event) {
        satchelOffset = SATCHEL_OFFSET.get();
        offsetOption.set(satchelOffset);

        if(event.getConfig().getType() == ModConfig.Type.CLIENT && Minecraft.getInstance().getConnection() != null) {
            SatchelData.get(Minecraft.getInstance().player).setHotbarOffset(satchelOffset);
            PacketDistributor.sendToServer(new SatchelOffsetUpdatePacketC2S(satchelOffset));
        }
    }

    public static void load(IEventBus modEventBus) {
        modEventBus.addListener(SatchelsClientConfig::onConfigUpdate);
    }
}
