package de.cheaterpaul.enchantmentmachine.block.entity;

import java.util.Optional;

public interface IEnchantmentMachine {

    Optional<StorageBlockEntity> getConnectedEnchantmentTE();

    /**
     * @return Whether an associated block pos is present. In most cases this should mean that getConnectedEnchantmentTE returns non empty TE
     */
    boolean hasConnectedTE();
}
