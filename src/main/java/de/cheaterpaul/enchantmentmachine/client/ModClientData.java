package de.cheaterpaul.enchantmentmachine.client;

import de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory.DisenchanterScreen;
import de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory.EnchanterScreen;
import de.cheaterpaul.enchantmentmachine.client.renderer.blockentity.EnchantmentBlockTileEntityRenderer;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;

@OnlyIn(Dist.CLIENT)
public class ModClientData {

    public static void registerScreens() {
        MenuScreens.register(ModData.enchanter_container.get(), EnchanterScreen::new);
        MenuScreens.register(ModData.disenchanter_container.get(), DisenchanterScreen::new);
    }

    public static void registerTileEntityRenderer() {
        BlockEntityRenderers.register(ModData.storage_tile.get(), EnchantmentBlockTileEntityRenderer::new);
    }
}
