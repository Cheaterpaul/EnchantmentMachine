package de.cheaterpaul.enchantmentmachine.server;

import de.cheaterpaul.enchantmentmachine.block.entity.EnchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class ServerPayloadHandler {

    public static void handleEnchantingPacket(EnchantingPacket enchantingPacket, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof EnchanterContainerMenu enchanter) {
                Optional<EnchanterBlockEntity> tile = enchanter.getWorldPosCallable().evaluate((world, pos) -> {
                    BlockEntity tile2 = world.getBlockEntity(pos);
                    if (tile2 instanceof EnchanterBlockEntity) {
                        return ((EnchanterBlockEntity) tile2);
                    }
                    return null;
                });
                tile.ifPresent(enchanterBlockEntity -> enchanterBlockEntity.executeEnchantments(context.player(), enchantingPacket.enchantments()));
            }
        });
    }
}
