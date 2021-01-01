package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;

public class EnchanterContainer extends EnchantmentBaseContainer {
    public EnchanterContainer(int id, PlayerInventory playerInventory) {
        this(id, new Inventory(1), playerInventory);
    }

    public EnchanterContainer(int id, IInventory inventory, PlayerInventory playerInventory) {
        super(ModData.enchanter_container, id, 1);
        this.addSlot(new Slot(inventory, 0, 147, 19));
        this.addPlayerSlots(playerInventory,8,140);
    }
}
