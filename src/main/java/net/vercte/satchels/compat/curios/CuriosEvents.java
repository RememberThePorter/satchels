package net.vercte.satchels.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.TriState;
import net.vercte.satchels.ModTags;
import net.vercte.satchels.satchel.SatchelData;
import top.theillusivec4.curios.api.event.CurioCanUnequipEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public class CuriosEvents {
    // TODO: Separate Curios Compat when I make it optional
    public static void canUnequipSatchel(final CurioCanUnequipEvent event) {
        if(!event.getStack().is(ModTags.SATCHEL)) return;

        if(!(event.getSlotContext().entity() instanceof Player player)) return;

        SatchelData satchelData = SatchelData.get(player);
        boolean isEmpty = satchelData.getSatchelInventory().isEmpty();
        event.setUnequipResult(isEmpty ? TriState.DEFAULT : TriState.FALSE);
    }

    public static void curioChangeMaybeSatchel(final CurioChangeEvent event) {
        if(!event.getFrom().is(ModTags.SATCHEL)) return;
        if(event.getTo().is(ModTags.SATCHEL)) return;

        if(!(event.getEntity() instanceof Player player)) return;

        SatchelData satchelData = SatchelData.get(player);
        satchelData.getSatchelInventory().dropAll(false);
        satchelData.setActive(false, true);
        satchelData.sendData();
    }
}
