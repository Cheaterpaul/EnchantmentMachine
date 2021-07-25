package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchanterTileEntity;
import de.cheaterpaul.enchantmentmachine.tiles.StorageTileEntity;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class EnchanterContainer extends EnchantmentBaseContainer implements StorageTileEntity.IEnchantmentListener {

    private final ContainerLevelAccess worldPosCallable;
    private final Player player;
    private ISlotListener listener;
    private final Container inventory;

    public EnchanterContainer(int id, Inventory playerInventory) {
        this(id, new SimpleContainer(1), playerInventory, ContainerLevelAccess.NULL);
    }

    public EnchanterContainer(int id, Container inventory, Inventory playerInventory, ContainerLevelAccess worldPosCallable) {
        super(ModData.enchanter_container, id, 1);
        this.inventory = inventory;
        this.addSlot(new Slot(inventory, 0, 203, 19) {

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                if (EnchanterContainer.this.listener != null) {
                    EnchanterContainer.this.listener.slotChanged();
                }
            }
        });
        this.addPlayerSlots(playerInventory, 28 + 8, 140 + 19);
        this.player = playerInventory.player;
        this.worldPosCallable = worldPosCallable;
        contactEnchantmentTileEntity(t -> t.registerListener(this));
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void removed(@Nonnull Player playerIn) {
        super.removed(playerIn);
        contactEnchantmentTileEntity(t -> t.removeListener(this));
        this.worldPosCallable.execute((world, pos) -> {
            this.clearContainer(playerIn, this.inventory);
        });
    }

    @Override
    public void onEnchantmentsChanged(Object2IntMap<EnchantmentInstanceMod> updatedList) {
        if (player instanceof ServerPlayer) {
            EnchantmentPacket p = new EnchantmentPacket(updatedList, false);
            EnchantmentMachineMod.DISPATCHER.sendTo(p, (ServerPlayer) player);
        }
    }

    public void setListener(ISlotListener listener) {
        this.listener = listener;
    }

    public ContainerLevelAccess getWorldPosCallable() {
        return worldPosCallable;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player playerEntity, int index) {
        return super.quickMoveStack(playerEntity, index);
    }

    private void contactEnchantmentTileEntity(Consumer<StorageTileEntity> consumer) {
        this.worldPosCallable.execute((w, p) -> {
            BlockEntity t = w.getBlockEntity(p);
            if (t instanceof EnchanterTileEntity) {
                ((EnchanterTileEntity) t).getConnectedEnchantmentTE().ifPresent(consumer);
            }
        });
    }

    @FunctionalInterface
    public interface ISlotListener {
        void slotChanged();
    }
}
