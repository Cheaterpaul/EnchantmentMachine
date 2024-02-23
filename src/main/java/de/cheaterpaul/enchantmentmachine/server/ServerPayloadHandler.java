package de.cheaterpaul.enchantmentmachine.server;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.block.entity.EnchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Optional;

public class ServerPayloadHandler {

    private static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

    public static ServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleEnchantingPacket(EnchantingPacket enchantingPacket, PlayPayloadContext playPayloadContext) {
        playPayloadContext.workHandler().execute(() -> {
            playPayloadContext.player().ifPresent(player -> {
                if (player.containerMenu instanceof EnchanterContainerMenu enchanter) {
                    Optional<EnchanterBlockEntity> tile = enchanter.getWorldPosCallable().evaluate((world, pos) -> {
                        BlockEntity tile2 = world.getBlockEntity(pos);
                        if (tile2 instanceof EnchanterBlockEntity) {
                            return ((EnchanterBlockEntity) tile2);
                        }
                        return null;
                    });
                    tile.ifPresent(enchanterBlockEntity -> enchanterBlockEntity.executeEnchantments(player, enchantingPacket.enchantments()));
                }
            });
        });
    }
}
