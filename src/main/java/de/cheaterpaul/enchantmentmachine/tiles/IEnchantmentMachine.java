package de.cheaterpaul.enchantmentmachine.tiles;

import java.util.Optional;

public interface IEnchantmentMachine {
    
    Optional<EnchantmentTileEntity> getConnectedEnchantmentTE();
}
