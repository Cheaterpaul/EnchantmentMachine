package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.entity.player.PlayerInventory;

public class EnchantmentContainer extends EnchantmentBaseContainer {
    public EnchantmentContainer(int id, PlayerInventory playerInventory) {
        super(ModData.enchantment_container, id);
    }
}
