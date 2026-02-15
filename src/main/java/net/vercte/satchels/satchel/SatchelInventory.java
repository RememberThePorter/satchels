package net.vercte.satchels.satchel;

import com.mojang.logging.LogUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SatchelInventory implements Container, INBTSerializable<CompoundTag> {
    private final String KEY_ITEMS = "Items";
    private final String KEY_SLOT = "Slot";
    private final int SATCHEL_SIZE = 6;

    private final SatchelData parent;
    private final List<ItemStack> items;

    public SatchelInventory(SatchelData parent) {
        this.parent = parent;
        this.items = NonNullList.withSize(SATCHEL_SIZE, ItemStack.EMPTY);
    }

    public SatchelInventory copy(SatchelData parent) {
        SatchelInventory copied = new SatchelInventory(parent);
        for(int i = 0; i < this.items.size(); i++) {
            copied.setItem(i, this.items.get(i).copy());
        }

        return copied;
    }

    // region Container
    @Override
    public int getContainerSize() { return SATCHEL_SIZE; }

    @Override
    @NotNull
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    @NotNull
    public ItemStack removeItem(int slot, int amount) {
        return !this.items.get(slot).isEmpty() ? ContainerHelper.removeItem(items, slot, amount) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = this.items.get(slot);
        this.items.set(slot, ItemStack.EMPTY);
        return removed;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player.canInteractWithEntity(this.parent.getPlayer(), 4.0F);
    }
    // endregion Container

    // region Inventory Parity
    public void removeItem(ItemStack item) {
        for(int i = 0; i < this.items.size(); i++) {
            if(this.items.get(i) == item) {
                this.items.set(i, ItemStack.EMPTY);
            }
        }
    }

    public boolean add(ItemStack stack) {
        return this.add(-1, stack);
    }

    public boolean add(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            Player player = this.parent.getPlayer();
            try {
                if (stack.isDamaged()) {
                    if (slot == -1) {
                        slot = this.getFreeSlot();
                    }

                    if (slot >= 0) {
                        this.items.set(slot, stack.copyAndClear());
                        this.items.get(slot).setPopTime(5);
                        return true;
                    } else if (player.hasInfiniteMaterials()) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int count;
                    do {
                        count = stack.getCount();
                        if (slot == -1) {
                            stack.setCount(this.addResource(stack));
                        } else {
                            stack.setCount(this.addResource(slot, stack));
                        }
                    } while (!stack.isEmpty() && stack.getCount() < count);

                    if (stack.getCount() == count && player.hasInfiniteMaterials()) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return stack.getCount() < count;
                    }
                }
            } catch (Throwable var6) {
                CrashReport crashReport = CrashReport.forThrowable(var6, "net.vercte.satchels: Adding item to satchel inventory");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Item being added");
                crashReportCategory.setDetail("Item ID", Item.getId(stack.getItem()));
                crashReportCategory.setDetail("Item data", stack.getDamageValue());
                crashReportCategory.setDetail("Item name", () -> stack.getHoverName().getString());
                throw new ReportedException(crashReport);
            }
        }
    }

    private int addResource(ItemStack itemStack) {
        int slot = this.getSlotWithRemainingSpace(itemStack);
        if (slot == -1) {
            slot = this.getFreeSlot();
        }

        return slot == -1 ? itemStack.getCount() : this.addResource(slot, itemStack);
    }

    private int addResource(int slot, ItemStack itemStack) {
        int count = itemStack.getCount();
        ItemStack containedItem = this.getItem(slot);
        if (containedItem.isEmpty()) {
            containedItem = itemStack.copyWithCount(0);
            this.setItem(slot, containedItem);
        }

        int max = this.getMaxStackSize(containedItem) - containedItem.getCount();
        int added = Math.min(count, max);
        if (added != 0) {
            count -= added;
            containedItem.grow(added);
            containedItem.setPopTime(5);
        }
        return count;
    }

    public void dropAll(boolean died) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                this.parent.getPlayer().drop(itemStack, died, !died);
                items.set(i, ItemStack.EMPTY);
            }
        }
    }

    public boolean placeItemBackInInventory(ItemStack inserted) {
        while(!inserted.isEmpty()) {
            int slot = this.getSlotWithRemainingSpace(inserted);
            if (slot == -1) {
                slot = this.getFreeSlot();
            }

            if (slot == -1) break;

            int j = inserted.getMaxStackSize() - this.getItem(slot).getCount();
            this.add(slot, inserted.split(j));
        }
        return inserted.isEmpty();
    }

    public int getSlotWithRemainingSpace(ItemStack inserted) {
        int selected = getSelectedSlot();
        if(selected != -1 && stackCanFitMore(items.get(selected), inserted)) return selected;

        for(int i = 0; i < this.items.size(); i++) {
            ItemStack here = this.items.get(i);
            if(this.stackCanFitMore(here, inserted)) return i;
        }
        return -1;
    }

    public boolean stackCanFitMore(ItemStack original, ItemStack inserted) {
        return !original.isEmpty() &&
                ItemStack.isSameItemSameComponents(original, inserted) &&
                original.isStackable() &&
                original.getCount() < this.getMaxStackSize(original);
    }

    public int getFreeSlot() {
        for(int i = 0; i < this.items.size(); i++) {
            if(this.items.get(i).isEmpty()) return i;
        }
        return -1;
    }

    private int getSelectedSlot() {
        int invSelected = this.parent.getPlayer().getInventory().selected;
        if(this.parent.isSlotInSatchel(invSelected)) return this.parent.convertToSatchelIndex(invSelected);
        return -1;
    }
    // endregion

    // region Serialization
    @Override
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        ListTag listTag = new ListTag();
        LogUtils.getLogger().info("Saving the satchel");

        for(int i = 0; i < this.items.size(); i++) {
            ItemStack slotContent = this.items.get(i);
            LogUtils.getLogger().info("stack: {}", slotContent);
            if (!slotContent.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt(KEY_SLOT, i);
                listTag.add(slotContent.save(provider, itemTag));
            }
        }

        CompoundTag tag = new CompoundTag();
        tag.put(KEY_ITEMS, listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag tag) {
        ListTag tagList = tag.getList(KEY_ITEMS, ListTag.TAG_COMPOUND);

        for(int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt(KEY_SLOT);
            if (slot >= 0 && slot < this.items.size()) {
                ItemStack.parse(provider, itemTags).ifPresent((stack) -> this.items.set(slot, stack));
            }
        }
    }

    public void serializeIntoByteBuf(RegistryFriendlyByteBuf byteBuf) {
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(byteBuf, this.items);
    }

    public void deserializeFromByteBuf(RegistryFriendlyByteBuf byteBuf) {
        List<ItemStack> newItems = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(byteBuf);
        for(int i = 0; i < this.items.size(); i++) {
            this.items.set(i, newItems.get(i));
        }
    }
    // endregion

    public SatchelData getParent() {
        return parent;
    }
    public List<ItemStack> getItems() { return items; }
}
