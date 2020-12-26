package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class DisenchanterContainer extends EnchantmentBaseContainer {
    public DisenchanterContainer(int id, PlayerInventory playerInventory) {
        super(ModData.disenchanter_container, id);
    }
}
