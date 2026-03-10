package net.vercte.satchels.satchel;

import net.minecraft.world.inventory.Slot;

import java.util.function.Consumer;

public class MenuWithSatchel {
    public static void addInventorySlots(SatchelData satchelData, Consumer<Slot> consumer, int x, int y, int padding) {
        for(int i = 0; i < satchelData.getSatchelInventory().getContainerSize(); i++) {
            int xPos = x + i * padding;
            SatchelInventorySlot slot = new SatchelInventorySlot(satchelData.getSatchelInventory(), i, xPos, y);
            slot.updateX();
            consumer.accept(slot);
        }
    }
}