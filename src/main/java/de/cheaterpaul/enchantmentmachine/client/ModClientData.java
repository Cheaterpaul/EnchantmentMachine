package de.cheaterpaul.enchantmentmachine.client;

import de.cheaterpaul.enchantmentmachine.client.screen.DisenchanterScreen;
import de.cheaterpaul.enchantmentmachine.client.screen.EnchanterScreen;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModClientData {

    public static void registerScreens() {
        ScreenManager.registerFactory(ModData.enchanter_container, EnchanterScreen::new);
        ScreenManager.registerFactory(ModData.disenchanter_container, DisenchanterScreen::new);
    }

}
