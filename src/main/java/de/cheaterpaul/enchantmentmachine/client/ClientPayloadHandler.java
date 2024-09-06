package de.cheaterpaul.enchantmentmachine.client;

import de.cheaterpaul.enchantmentmachine.client.gui.screens.StorageScreen;
import de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory.EnchanterScreen;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    public static void handleEnchantmentPacket(EnchantmentPacket a, IPayloadContext b) {
        b.enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof StorageScreen) {
                ((StorageScreen) Minecraft.getInstance().screen).updateEnchantments(a.enchantments());
            } else if (Minecraft.getInstance().screen instanceof EnchanterScreen) {
                ((EnchanterScreen) Minecraft.getInstance().screen).updateEnchantments(a.enchantments());
            } else if (a.shouldOpenEnchantmentListScreen()) {
                StorageScreen screen = new StorageScreen();
                Minecraft.getInstance().setScreen(screen);
                screen.updateEnchantments(a.enchantments());
            }
        });
    }
}
