package de.cheaterpaul.enchantmentmachine.client;

import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = REFERENCE.MODID, dist = Dist.CLIENT)
public class EnchantmentMachineClientMod {

    public EnchantmentMachineClientMod(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(ModClientData::registerScreens);
        modEventBus.addListener(ModClientData::registerTileEntityRenderer);
    }
}
