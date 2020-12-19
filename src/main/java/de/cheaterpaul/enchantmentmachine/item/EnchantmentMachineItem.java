package de.cheaterpaul.enchantmentmachine.item;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class EnchantmentMachineItem extends BlockItem {

    public EnchantmentMachineItem(Properties properties) {
        super(ModData.enchantment_machine_block, properties);
    }
}
