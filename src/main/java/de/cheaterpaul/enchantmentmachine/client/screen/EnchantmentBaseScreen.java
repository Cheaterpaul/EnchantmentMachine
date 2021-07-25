package de.cheaterpaul.enchantmentmachine.client.screen;

import de.cheaterpaul.enchantmentmachine.inventory.EnchantmentBaseContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EnchantmentBaseScreen<T extends EnchantmentBaseContainer> extends AbstractContainerScreen<T> {

    public EnchantmentBaseScreen(T container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
    }

}
