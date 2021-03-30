package de.cheaterpaul.enchantmentmachine.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public abstract class EnchantmentBaseContainer extends Container {

    private final int size;

    /**
     * @param sizeInventory inventory size of the container except the player slots
     */
    public EnchantmentBaseContainer(ContainerType<?> containerType, int id, int sizeInventory) {
        super(containerType, id);
        this.size = sizeInventory;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    protected void addPlayerSlots(PlayerInventory playerInventory, int baseX, int baseY) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, baseX + j * 18, baseY + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, baseX + i * 18, baseY + 58));
        }
    }

    protected void addPlayerSlots(PlayerInventory playerInventory) {
        this.addPlayerSlots(playerInventory, 8, 84);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();
            if (index < this.size) {
                if (!this.mergeItemStack(slotStack, this.size, 36 + this.size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= this.size && index < 27 + this.size) {
                if (!this.mergeItemStack(slotStack, 0, this.size, false)) {
                    if (slotStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
                if (!this.mergeItemStack(slotStack, 27 + this.size, 36 + this.size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 + this.size && index < 36 + this.size) {
                if (this.mergeItemStack(slotStack, 0, this.size, false)) {
                    return ItemStack.EMPTY;
                }
                if (!this.mergeItemStack(slotStack, this.size + 1, 27 + this.size, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
            detectAndSendChanges();
        }

        return result;
    }
}
