package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class EnchanterContainer extends EnchantmentBaseContainer {
    public EnchanterContainer(int id, PlayerInventory playerInventory) {
        this(id, new Inventory(1), playerInventory);
    }

    public EnchanterContainer(int id, IInventory inventory, PlayerInventory playerInventory) {
        super(ModData.enchanter_container, id);
        this.addSlot(new Slot(inventory, 0, 400, 20));
        this.addPlayerSlots(playerInventory);
    }
}
