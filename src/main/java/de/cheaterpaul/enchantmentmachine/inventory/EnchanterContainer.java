package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantmentPacket;
import de.cheaterpaul.enchantmentmachine.tiles.EnchanterTileEntity;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentTileEntity;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

import java.util.function.Consumer;

public class EnchanterContainer extends EnchantmentBaseContainer implements EnchantmentTileEntity.IEnchantmentListener {

    private final IWorldPosCallable worldPosCallable;
    private final PlayerEntity player;
    private ISlotListener listener;

    public EnchanterContainer(int id, PlayerInventory playerInventory) {
        this(id, new Inventory(1), playerInventory, IWorldPosCallable.DUMMY);
    }

    public EnchanterContainer(int id, IInventory inventory, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(ModData.enchanter_container, id, 1);
        this.addSlot(new Slot(inventory, 0, 203, 19) {

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
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

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        contactEnchantmentTileEntity(t -> t.removeListener(this));
    }

    @Override
    public void onEnchantmentsChanged(Object2IntMap<EnchantmentInstance> updatedList) {
        if (player instanceof ServerPlayerEntity) {
            EnchantmentPacket p = new EnchantmentPacket(updatedList, false);
            EnchantmentMachineMod.DISPATCHER.sendTo(p, (ServerPlayerEntity) player);
        }
    }

    public void setListener(ISlotListener listener) {
        this.listener = listener;
    }

    public IWorldPosCallable getWorldPosCallable() {
        return worldPosCallable;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        return super.transferStackInSlot(playerEntity, index);
    }

    private void contactEnchantmentTileEntity(Consumer<EnchantmentTileEntity> consumer) {
        this.worldPosCallable.consume((w, p) -> {
            TileEntity t = w.getTileEntity(p);
            if (t instanceof EnchanterTileEntity) {
                ((EnchanterTileEntity) t).getConnectedEnchantmentTE().ifPresent(consumer);
            }
        });
    }

    @FunctionalInterface
    public interface ISlotListener {
        void slotChanged();
    }
//
//    public static class Factory implements IContainerFactory<EnchanterContainer> {
//
//        @Nullable
//        @Override
//        public EnchanterContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
//            if (data == null)
//                return new EnchanterContainer(windowId, inv);
//            boolean extraSlots = data.readBoolean(); //Anything read here has to be written to buffer in open method (in EnchantmentBlock)
//            EnchanterContainer c =  new EnchanterContainer(windowId, inv, IWorldPosCallable.DUMMY, new Inventory(extraSlots ? 8 : 6), extraSlots, null);
//        }
//    }
}
