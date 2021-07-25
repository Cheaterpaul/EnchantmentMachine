package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class EnchantmentBaseTileEntity extends BaseContainerBlockEntity implements IEnchantmentMachine {

    /**
     * Stores the last known location of an adjacent enchantment storage block.
     * Set when this TE receives its world and position.
     * Updated on neighbour changed (triggered from block)
     */
    @Nullable
    private BlockPos storageBlockPos;


    public EnchantmentBaseTileEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
    }

    @Override
    public boolean stillValid(@Nonnull Player playerEntity) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return playerEntity.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }


    @Override
    public Optional<StorageTileEntity> getConnectedEnchantmentTE() {
        if (storageBlockPos == null) return Optional.empty();
        BlockEntity te = this.level.getBlockEntity(storageBlockPos);
        if (te instanceof StorageTileEntity) {
            return Optional.of((StorageTileEntity) te);
        }
        return Optional.empty();
    }

    public void onNeighbourChanged(LevelReader iWorld, BlockPos neighborPos) {
        if (this.storageBlockPos == null) {
            if (iWorld.getBlockState(neighborPos).getBlock() == ModData.storage_block) {
                this.storageBlockPos = neighborPos;
            }
        } else if (this.storageBlockPos.equals(neighborPos)) {
            if (iWorld.getBlockState(neighborPos).getBlock() != ModData.storage_block) {
                this.storageBlockPos = null;
            }
        }
    }

    @Override
    public boolean hasConnectedTE() {
        return storageBlockPos != null;
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        super.save(compound);
        if (this.storageBlockPos != null) {
            compound.putIntArray("storageblock", new int[]{this.storageBlockPos.getX(), this.storageBlockPos.getY(), this.storageBlockPos.getZ()});
        }
        return compound;
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("storageblock")) {
            int[] pos = nbt.getIntArray("storageblock");
            this.storageBlockPos = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }
}
