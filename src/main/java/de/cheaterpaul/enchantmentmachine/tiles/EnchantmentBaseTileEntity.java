package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class EnchantmentBaseTileEntity extends LockableTileEntity implements IEnchantmentMachine{

    /**
     * Stores the last known location of an adjacent enchantment storage block.
     * Set when this TE receives its world and position.
     * Updated on neighbour changed (triggered from block)
     */
    @Nullable
    private BlockPos storageBlockPos;


    public EnchantmentBaseTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity playerEntity) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return playerEntity.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }


    @Override
    public Optional<StorageTileEntity> getConnectedEnchantmentTE() {
        if (storageBlockPos == null) return Optional.empty();
        TileEntity te = this.world.getTileEntity(storageBlockPos);
        if (te instanceof StorageTileEntity) {
            return Optional.of((StorageTileEntity) te);
        }
        return Optional.empty();
    }

    public void onNeighbourChanged(IWorldReader iWorld, BlockPos neighborPos) {
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
        return storageBlockPos!=null;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (this.storageBlockPos != null) {
            compound.putIntArray("storageblock", new int[]{this.storageBlockPos.getX(), this.storageBlockPos.getY(), this.storageBlockPos.getZ()});
        }
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        if (nbt.contains("storageblock")) {
            int[] pos = nbt.getIntArray("storageblock");
            this.storageBlockPos = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }
}
