package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.enchantmentmachine.inventory.EnchantmentBaseContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EnchantmentBaseScreen<T extends EnchantmentBaseContainer> extends ContainerScreen<T> {

    public EnchantmentBaseScreen(T container, PlayerInventory playerInventory, ITextComponent name) {
        super(container, playerInventory, name);
    }

}
