package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class DisenchanterContainer extends EnchantmentBaseContainer {

    public DisenchanterContainer(int id, PlayerInventory playerInventory) {
        this(id, new Inventory(2), playerInventory);
    }

    public DisenchanterContainer(int id, IInventory inventory, PlayerInventory playerInventory) {
        super(ModData.disenchanter_container, id, 2);
        this.addSlot(new Slot(inventory, 0, 80, 17) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return !EnchantmentHelper.getEnchantments(itemStack).isEmpty() && (ModConfig.SERVER.allowDisenchantingItems.get() || !EnchantedBookItem.getEnchantments(itemStack).isEmpty());
            }
        });
        this.addSlot(new Slot(inventory, 1, 80, 53) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }
        });
        inventory.startOpen(playerInventory.player);
        this.addPlayerSlots(playerInventory);
    }

}
