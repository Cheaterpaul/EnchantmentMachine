package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.entity.player.PlayerInventory;

public class EnchanterContainer extends EnchantmentBaseContainer {
    public EnchanterContainer(int id, PlayerInventory playerInventory) {
        super(ModData.enchanter_container, id);
    }
}
