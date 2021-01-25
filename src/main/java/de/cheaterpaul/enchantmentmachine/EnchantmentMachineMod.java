package de.cheaterpaul.enchantmentmachine;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.data.ModDataGenerator;
import de.cheaterpaul.enchantmentmachine.network.AbstractPacketDispatcher;
import de.cheaterpaul.enchantmentmachine.network.ModPacketDispatcher;
import de.cheaterpaul.enchantmentmachine.proxy.ClientProxy;
import de.cheaterpaul.enchantmentmachine.proxy.Proxy;
import de.cheaterpaul.enchantmentmachine.proxy.ServerProxy;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(REFERENCE.MODID)
public class EnchantmentMachineMod {

    public static final Proxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, ()-> ServerProxy::new);
    public static final AbstractPacketDispatcher DISPATCHER = new ModPacketDispatcher();
    public static final ItemGroup CREATIVE_TAB = new ItemGroup(REFERENCE.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModData.enchantment_block);
        }
    };

    public EnchantmentMachineMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.register(ModData.class);
        bus.addListener(ModDataGenerator::gatherData);
        bus.addListener(this::onLoadComplete);
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(PROXY::onTextureStitchEvent);
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
        PROXY.onLoadComplete();
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        PROXY.onClientSetup();
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        DISPATCHER.registerPackets();
    }
}
