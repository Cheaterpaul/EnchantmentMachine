package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nonnull;
import java.util.Map;

public class DisenchanterContainerMenu extends EnchantmentBaseContainerMenu {

    public DisenchanterContainerMenu(int id, Inventory playerInventory) {
        this(id, new SimpleContainer(2), playerInventory);
    }

    public DisenchanterContainerMenu(int id, Container inventory, Inventory playerInventory) {
        super(ModData.disenchanter_container.get(), id, 2);
        this.addSlot(new Slot(inventory, 0, 80, 17) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack itemStack) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
                if (!enchantments.isEmpty()) {
                    if (ModConfig.SERVER.allowDisenchantingItems.get() || !EnchantedBookItem.getEnchantments(itemStack).isEmpty()) {
                        return ModConfig.SERVER.allowDisenchantingCurses.get() || !enchantments.entrySet().stream().allMatch(a -> a.getKey().isCurse());
                    }
                }
                return false;
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
