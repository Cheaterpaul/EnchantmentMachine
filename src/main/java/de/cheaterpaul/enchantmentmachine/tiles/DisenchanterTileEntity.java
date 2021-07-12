package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainer;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class DisenchanterTileEntity extends EnchantmentBaseTileEntity implements ITickableTileEntity, ISidedInventory, IHopper {

    private static final ITextComponent name = Utils.genTranslation("tile", "disenchanter.name");
    private static final int DURATION = 20;
    private final LazyOptional<? extends IItemHandler>[] itemHandler = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    /**
     * Countdown to disenchantment
     * >0 if waiting
     */
    private int timer;
    private int transferCooldown;

    public DisenchanterTileEntity() {
        super(ModData.disenchanter_tile);
    }

    @Nonnull
    @Override
    protected ITextComponent getDefaultName() {
        return name;
    }

    @Nonnull
    @Override
    protected Container createMenu(int i, @Nonnull PlayerInventory playerInventory) {
        return new DisenchanterContainer(i, this, playerInventory);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack getItem(int i) {
        return this.inventory.get(i);
    }

    @Nonnull
    @Override
    public ItemStack removeItem(int i, int i1) {
        ItemStack result = ItemStackHelper.removeItem(this.inventory, i, i1);
        this.setTimer();
        return result;
    }

    @Nonnull
    @Override
    public ItemStack removeItemNoUpdate(int i) {
        ItemStack stack = ItemStackHelper.takeItem(this.inventory, i);
        this.setTimer();
        return stack;
    }

    @Override
    public void setItem(int i, @Nonnull ItemStack itemStack) {
        this.inventory.set(i, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        setTimer();
    }


    private void setTimer() {
        if (!getItem(0).isEmpty() && (getItem(1).isEmpty() || (resultItem(getItem(0)).sameItem(getItem(1)) && getItem(1).getCount() + 1 <= getItem(1).getMaxStackSize()))) {
            this.timer = DURATION;
        } else {
            this.timer = 0;
        }
    }

    private ItemStack resultItem(ItemStack stack) {
        if (stack.getItem() instanceof EnchantedBookItem) {
            return new ItemStack(Items.BOOK);
        }
        ItemStack stack1 = stack.copy();
        stack1.getOrCreateTag().remove("StoredEnchantments");
        EnchantmentHelper.setEnchantments(Collections.emptyMap(), stack1);
        return stack1;
    }

    private Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
        Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTags());
        map.putAll(EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(stack)));
        return map;
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public double getLevelX() {
        return this.worldPosition.getX() + 0.5;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{1};
        }
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
        if (index == 0) {
            if (!ModConfig.SERVER.allowDisenchantingItems.get()) {
                if (EnchantedBookItem.getEnchantments(stack).isEmpty()) return false;
            }
            return !EnchantmentHelper.getEnchantments(stack).isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @Nonnull ItemStack itemStackIn, @Nullable Direction direction) {
        return index == 0 && canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @Nonnull ItemStack stack, @Nonnull Direction direction) {
        return index == 1;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.inventory.clear();
        ItemStackHelper.loadAllItems(nbt, this.inventory);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        ItemStackHelper.saveAllItems(compound, this.inventory);
        return compound;
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        //noinspection ConstantConditions
        this.load(this.level.getBlockState(pkt.getPos()), pkt.getTag());
    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction facing) {
        if (!this.remove && facing != null && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == Direction.UP)
                return itemHandler[0].cast();
            else if (facing == Direction.DOWN)
                return itemHandler[1].cast();
            else
                return itemHandler[2].cast();
        }
        return super.getCapability(cap, facing);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        for (LazyOptional<? extends IItemHandler> opt : itemHandler) {
            opt.invalidate();
        }
    }

    @Override
    public double getLevelY() {
        return this.worldPosition.getY() + 0.5;
    }

    @Override
    public double getLevelZ() {
        return this.worldPosition.getZ() + 0.5;
    }

    @Override
    public void tick() {
        if (this.timer > 0 && this.hasConnectedTE()) {
            if (--this.timer == 0) {
                getConnectedEnchantmentTE().ifPresent(te -> {
                    ItemStack stack = this.inventory.get(0);
                    if (canDisenchant(stack)) {
                        Map<Enchantment, Integer> map = getEnchantments(stack);
                        map.forEach((key, value) -> {
                            EnchantmentInstance inst = new EnchantmentInstance(key, value);
                            te.addEnchantment(inst);
                        });
                        stack = resultItem(stack);
                        ItemStack slot = getItem(1);
                        if (!slot.isEmpty() && slot.sameItem(stack)) {
                            stack.shrink(-slot.getCount());
                        }
                        setItem(1, stack);
                        setItem(0, ItemStack.EMPTY);
                    }
                });
            }
        }
        if (this.level != null && !this.level.isClientSide) {
            --this.transferCooldown;
            if (transferCooldown <= 0) {
                this.transferCooldown = 0;
                if (HopperTileEntity.suckInItems(this)) {
                    this.transferCooldown = 0;
                }
            }
        }
    }

    private boolean canDisenchant(ItemStack stack) {
        return ModConfig.SERVER.allowDisenchantingItems.get() || !EnchantedBookItem.getEnchantments(stack).isEmpty();
    }
}
