package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public class EnchanterContainer extends EnchantmentBaseContainer {

    private final IWorldPosCallable worldPosCallable;
    private IListener listener;

    public EnchanterContainer(int id, PlayerInventory playerInventory) {
        this(id, new Inventory(1), playerInventory, IWorldPosCallable.DUMMY);
    }

    public EnchanterContainer(int id, IInventory inventory, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(ModData.enchanter_container, id, 1);
        this.addSlot(new Slot(inventory, 0, 203, 19) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.isEnchantable();
            }

            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
                if (EnchanterContainer.this.listener != null) {
                    EnchanterContainer.this.listener.slotChanged();
                }
            }
        });
        this.addPlayerSlots(playerInventory,28 + 8,140 + 19);

        this.worldPosCallable = worldPosCallable;
    }

    public void setListener(IListener listener) {
        this.listener= listener;
    }

    public IWorldPosCallable getWorldPosCallable() {
        return worldPosCallable;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        return super.transferStackInSlot(playerEntity, index);
    }

    @FunctionalInterface
    public interface IListener {
        void slotChanged();
    }
}
