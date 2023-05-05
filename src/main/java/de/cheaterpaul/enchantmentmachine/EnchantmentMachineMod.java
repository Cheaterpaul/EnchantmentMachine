package de.cheaterpaul.enchantmentmachine;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.data.ModDataGenerator;
import de.cheaterpaul.enchantmentmachine.network.AbstractPacketDispatcher;
import de.cheaterpaul.enchantmentmachine.network.ModPacketDispatcher;
import de.cheaterpaul.enchantmentmachine.proxy.ClientProxy;
import de.cheaterpaul.enchantmentmachine.proxy.Proxy;
import de.cheaterpaul.enchantmentmachine.proxy.ServerProxy;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

@Mod(REFERENCE.MODID)
public class EnchantmentMachineMod {

    public static final Proxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static final AbstractPacketDispatcher DISPATCHER = new ModPacketDispatcher();

    public EnchantmentMachineMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModConfig.init();
        bus.addListener(ModDataGenerator::gatherData);
        bus.addListener(this::onLoadComplete);
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onRegisterTab);
        ModData.register(bus);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        PROXY.onClientSetup();
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        DISPATCHER.registerPackets();
    }

    private void onLoadComplete(FMLLoadCompleteEvent event) {
        PROXY.onLoadComplete();
    }

    private void onRegisterTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(REFERENCE.MODID, "main"), builder ->
                builder.icon(() -> ModData.storage_block.get().asItem().getDefaultInstance()).title(Component.translatable("itemGroup.enchantmentmachine")).displayItems((params, output) -> {
                    output.accept(ModData.enchanter_block.get());
                    output.accept(ModData.disenchanter_block.get());
                    output.accept(ModData.storage_block.get());
                }));
    }

}
