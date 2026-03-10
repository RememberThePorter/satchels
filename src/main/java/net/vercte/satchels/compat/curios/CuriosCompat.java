package net.vercte.satchels.compat.curios;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.vercte.satchels.ModSounds;
import net.vercte.satchels.ModTags;
import net.vercte.satchels.api.SatchelAccess;
import net.vercte.satchels.compat.CompatEntrypoint;
import net.vercte.satchels.satchel.SatchelData;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioCanUnequipEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class CuriosCompat implements CompatEntrypoint {
    @Override
    public void initialize() {
        SatchelAccess.CAN_ACCESS_PREDICATES.add(this::playerCanAccessSatchel);
        SatchelAccess.IS_VISIBLE_PREDICATES.add(this::playerSatchelIsVisible);

        NeoForge.EVENT_BUS.addListener(this::canUnequipSatchel);
        NeoForge.EVENT_BUS.addListener(this::curioChangeMaybeSatchel);
    }

    public boolean playerCanAccessSatchel(Player player) {
        Optional<ICuriosItemHandler> optCuriosInventory = CuriosApi.getCuriosInventory(player);

        if(optCuriosInventory.isEmpty()) return false;
        return optCuriosInventory.get().isEquipped(s -> s.is(ModTags.SATCHEL));
    }

    public boolean playerSatchelIsVisible(Player player) {
        Optional<ICuriosItemHandler> optCuriosInventory = CuriosApi.getCuriosInventory(player);

        if(optCuriosInventory.isEmpty()) return false;
        AtomicBoolean renders = new AtomicBoolean(false);
        optCuriosInventory.get().getCurios().forEach((s, c) -> {
            IDynamicStackHandler stackHandler = c.getStacks();

            for(int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack stack = stackHandler.getStackInSlot(i);

                if(stack.isEmpty()) return;
                if(!stack.is(ModTags.SATCHEL)) return;
                NonNullList<Boolean> renderStates = c.getRenders();
                boolean renderable = renderStates.size() > i && renderStates.get(i);
                if(renderable) renders.set(true);
            }
        });

        return renders.get();
    }

    public void canUnequipSatchel(final CurioCanUnequipEvent event) {
        if(!event.getStack().is(ModTags.SATCHEL)) return;

        if(!(event.getSlotContext().entity() instanceof Player player)) return;

        SatchelData satchelData = SatchelData.get(player);
        boolean isEmpty = satchelData.getSatchelInventory().isEmpty();
        event.setUnequipResult(isEmpty ? TriState.DEFAULT : TriState.FALSE);
    }

    public void curioChangeMaybeSatchel(final CurioChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        boolean satchelEquipped = !event.getFrom().is(ModTags.SATCHEL) && event.getTo().is(ModTags.SATCHEL);

        if (satchelEquipped) {
            float pitch = 0.9f + (player.getRandom().nextFloat() / 5);
            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    ModSounds.SATCHEL_EQUIP.get(), SoundSource.PLAYERS,
                    1, pitch
            );
            return;
        }

        if (event.getTo().is(ModTags.SATCHEL)) return;

        SatchelData satchelData = SatchelData.get(player);
        satchelData.getSatchelInventory().dropAll(false);
        satchelData.setActive(false, true);
        satchelData.sendData();
    }
}
