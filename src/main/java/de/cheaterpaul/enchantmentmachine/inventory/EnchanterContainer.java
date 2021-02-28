package de.cheaterpaul.enchantmentmachine.inventory;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public class EnchanterContainer extends EnchantmentBaseContainer {

    private final IWorldPosCallable worldPosCallable;
    private IListener listener;

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
        this.addPlayerSlots(playerInventory,28 + 8,140 + 19);

        this.worldPosCallable = worldPosCallable;
    }

    public void setListener(IListener listener) {
        this.listener= listener;
    }

    public IWorldPosCallable getWorldPosCallable() {
        return worldPosCallable;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        return super.transferStackInSlot(playerEntity, index);
    }

    @FunctionalInterface
    public interface IListener {
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
