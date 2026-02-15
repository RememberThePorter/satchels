package net.vercte.satchels.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.vercte.satchels.Satchels;
import net.vercte.satchels.client.model.SatchelLayer;
import net.vercte.satchels.client.satchel.SatchelHotbarOverlay;
import net.vercte.satchels.network.packets.ToggleSatchelPacketC2S;
import net.vercte.satchels.satchel.SatchelData;
import org.lwjgl.glfw.GLFW;

@Mod(value = Satchels.ID, dist = Dist.CLIENT)
public class SatchelsClient {
    public SatchelsClient(IEventBus modEventBus) {
        modEventBus.addListener(SatchelsClient::registerOverlays);

        modEventBus.addListener(SatchelsClient::addEntityRenderLayers);
        modEventBus.addListener(ModModels::onRegisterAdditional);
        modEventBus.addListener(ModModels::onBakingCompleted);

        NeoForge.EVENT_BUS.addListener(SatchelsClient::endClientTick);
    }

    public static final Lazy<KeyMapping> KEYMAPPING_TOGGLE_SATCHEL = Lazy.of(
            () -> new KeyMapping("key.satchels.toggle_satchel", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KeyMapping.CATEGORY_INVENTORY)
    );

    public static void registerOverlays(final RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Satchels.at(SatchelHotbarOverlay.ID), SatchelHotbarOverlay.INSTANCE::render);
    }

    public static void endClientTick(final ClientTickEvent.Post event) {
        while(KEYMAPPING_TOGGLE_SATCHEL.get().consumeClick()) {
            SatchelData satchelData = SatchelData.get(Minecraft.getInstance().player);

            if(!satchelData.canAccess()) continue;
            boolean willEnable = !satchelData.isActive();
            satchelData.setActive(willEnable, true);
            PacketDistributor.sendToServer(new ToggleSatchelPacketC2S(willEnable));
        }
    }

    public static void addEntityRenderLayers(final EntityRenderersEvent.AddLayers event) {
        ItemRenderer itemRenderer = event.getContext().getItemRenderer();
        EntityRenderDispatcher erDispatcher = event.getContext().getEntityRenderDispatcher();

        for(EntityRenderer<? extends Player> renderer : erDispatcher.getSkinMap().values()) {
            if(renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new SatchelLayer<>(playerRenderer, itemRenderer));
            }
        }
    }
}
