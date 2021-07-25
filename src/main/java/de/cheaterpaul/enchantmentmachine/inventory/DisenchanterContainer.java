package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nonnull;

public class DisenchanterContainer extends EnchantmentBaseContainer {

    public DisenchanterContainer(int id, Inventory playerInventory) {
        this(id, new SimpleContainer(2), playerInventory);
    }

    public DisenchanterContainer(int id, Container inventory, Inventory playerInventory) {
        super(ModData.disenchanter_container, id, 2);
        this.addSlot(new Slot(inventory, 0, 80, 17) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return !EnchantmentHelper.getEnchantments(itemStack).isEmpty() && (ModConfig.SERVER.allowDisenchantingItems.get() || !EnchantedBookItem.getEnchantments(itemStack).isEmpty());
            }
        });
        this.addSlot(new Slot(inventory, 1, 80, 53) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                return false;
            }
        });
        inventory.startOpen(playerInventory.player);
        this.addPlayerSlots(playerInventory);
    }

}
