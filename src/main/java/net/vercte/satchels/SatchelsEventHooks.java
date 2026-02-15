package net.vercte.satchels;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.vercte.satchels.satchel.SatchelData;

public class SatchelsEventHooks {
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if(!(player instanceof ServerPlayer sp)) return;

        SatchelData.get(sp).sendData();
    }
}
