package de.cheaterpaul.enchantmentmachine;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.data.ModDataGenerator;
import de.cheaterpaul.enchantmentmachine.network.ModPacketDispatcher;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(REFERENCE.MODID)
public class EnchantmentMachineMod {

    public EnchantmentMachineMod(IEventBus modBus) {
        ModConfig.init(modBus);
        modBus.addListener(ModDataGenerator::gatherData);
        ModData.register(modBus);
        modBus.register(ModPacketDispatcher.class);
    }

}
