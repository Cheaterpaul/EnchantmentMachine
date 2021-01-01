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
    public EnchanterContainer(int id, PlayerInventory playerInventory) {
        this(id, new Inventory(1), playerInventory, IWorldPosCallable.DUMMY);
    }

    public EnchanterContainer(int id, IInventory inventory, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(ModData.enchanter_container, id, 1);
        this.addSlot(new Slot(inventory, 0, 147, 19) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.isEnchantable();
            }
        });
        this.addPlayerSlots(playerInventory,8,140);

        this.worldPosCallable = worldPosCallable;
    }

    public IWorldPosCallable getWorldPosCallable() {
        return worldPosCallable;
    }
}
