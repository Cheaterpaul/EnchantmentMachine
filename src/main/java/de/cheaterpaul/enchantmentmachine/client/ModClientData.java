package de.cheaterpaul.enchantmentmachine.client;

import de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory.DisenchanterScreen;
import de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory.EnchanterScreen;
import de.cheaterpaul.enchantmentmachine.client.renderer.blockentity.EnchantmentBlockTileEntityRenderer;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.jetbrains.annotations.NotNull;

public class ModClientData {

    public static void registerTileEntityRenderer(EntityRenderersEvent.@NotNull RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModData.storage_tile.get(), EnchantmentBlockTileEntityRenderer::new);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModData.enchanter_container.get(), EnchanterScreen::new);
        event.register(ModData.disenchanter_container.get(), DisenchanterScreen::new);
    }
}
