package de.cheaterpaul.enchantmentmachine.proxy;

import de.cheaterpaul.enchantmentmachine.client.ModClientData;
import de.cheaterpaul.enchantmentmachine.client.screen.EnchantmentScreen;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements Proxy {

    @Override
    public void onLoadComplete() {
        ModClientData.registerScreens();
    }

    @Override
    public void handleEnchantmentpacket(EnchantmentPacket packet) {
        if (Minecraft.getInstance().currentScreen instanceof EnchantmentScreen) {
            ((EnchantmentScreen) Minecraft.getInstance().currentScreen).updateEnchantments(packet.getEnchantments());
        }
    }
}
