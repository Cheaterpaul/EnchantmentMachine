package de.cheaterpaul.enchantmentmachine.client;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import de.cheaterpaul.enchantmentmachine.client.screen.DisenchanterScreen;
import de.cheaterpaul.enchantmentmachine.client.screen.EnchanterScreen;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ModClientData {

    public static void registerScreens() {
        ScreenManager.registerFactory(ModData.enchanter_container, EnchanterScreen::new);
        ScreenManager.registerFactory(ModData.disenchanter_container, DisenchanterScreen::new);
    }

    public static void registerTileEntityRenderer(){
        ClientRegistry.bindTileEntityRenderer(ModData.enchantment_tile, EnchantmentBlockTileEntityRenderer::new);
    }

    public static void textureStitchEvent(TextureStitchEvent.Pre event) {
        event.addSprite(EnchantmentBlockTileEntityRenderer.TEXTURE_BOOK.getTextureLocation());
    }



}
