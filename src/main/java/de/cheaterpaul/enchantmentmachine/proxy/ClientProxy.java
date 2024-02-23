package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.client.ModClientData;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = REFERENCE.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModClientData.registerScreens();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLLoadCompleteEvent event) {
        ModClientData.registerTileEntityRenderer();
    }

}
