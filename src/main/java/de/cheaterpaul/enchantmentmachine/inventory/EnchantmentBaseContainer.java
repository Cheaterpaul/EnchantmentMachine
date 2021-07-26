package de.cheaterpaul.enchantmentmachine.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public abstract class EnchantmentBaseContainer extends AbstractContainerMenu {

    private final int size;

    /**
     * @param sizeInventory inventory size of the container except the player slots
     */
    public EnchantmentBaseContainer(MenuType<?> containerType, int id, int sizeInventory) {
        super(containerType, id);
        this.size = sizeInventory;
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }

    protected void addPlayerSlots(Inventory playerInventory, int baseX, int baseY) {
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

    protected void addPlayerSlots(Inventory playerInventory) {
        this.addPlayerSlots(playerInventory, 8, 84);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player playerEntity, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();
            if (index < this.size) {
                if (!this.moveItemStackTo(slotStack, this.size, 36 + this.size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 27 + this.size) {
                if (!this.moveItemStackTo(slotStack, 0, this.size, false)) {
                    if (slotStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
                if (!this.moveItemStackTo(slotStack, 27 + this.size, 36 + this.size, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 + this.size && index < 36 + this.size) {
                if (this.moveItemStackTo(slotStack, 0, this.size, false)) {
                    return ItemStack.EMPTY;
                }
                if (!this.moveItemStackTo(slotStack, this.size + 1, 27 + this.size, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerEntity, slotStack);
            broadcastChanges();
        }

        return result;
    }
}
