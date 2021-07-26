package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Random;
import java.util.WeakHashMap;

public class StorageTileEntity extends BlockEntity implements IEnchantmentMachine {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Component name = Utils.genTranslation("tile", "enchantment.name");
    private static final Random random = new Random();
    /**
     * Used as list, just using map because it implements all the required weak reference stuff
     */
    private final WeakHashMap<IEnchantmentListener, IEnchantmentListener> listeners = new WeakHashMap<>();
    private final Object2IntArrayMap<EnchantmentInstanceMod> enchantmentMaps = new Object2IntArrayMap<>();

    public int ticks;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float nextPageAngle;
    public float pageAngle;
    public float tRot;

    public StorageTileEntity(BlockPos blockPos, BlockState state) {
        super(ModData.storage_tile, blockPos, state);
    }

    public void addEnchantment(EnchantmentInstanceMod enchInst, int count) {
        int c = enchantmentMaps.getOrDefault(enchInst, 0);
        enchantmentMaps.put(enchInst, c + count);
        notifyListeners();
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState state, StorageTileEntity entity) {
        entity.pageTurningSpeed = entity.nextPageTurningSpeed;
        entity.pageAngle = entity.nextPageAngle;
        //noinspection ConstantConditions
        Player playerentity = entity.level.getNearestPlayer((double) entity.worldPosition.getX() + 0.5D, (double) entity.worldPosition.getY() + 0.5D, (double) entity.worldPosition.getZ() + 0.5D, 3.0D, false);
        if (playerentity != null) {
            double d0 = playerentity.getX() - ((double) entity.worldPosition.getX() + 0.5D);
            double d1 = playerentity.getZ() - ((double) entity.worldPosition.getZ() + 0.5D);
            entity.tRot = (float) Mth.atan2(d1, d0);
            entity.nextPageTurningSpeed += 0.1F;
            if (entity.nextPageTurningSpeed < 0.5F || random.nextInt(40) == 0) {
                float f1 = entity.flipT;

                do {
                    entity.flipT += (float) (random.nextInt(4) - random.nextInt(4));
                } while (f1 == entity.flipT);
            }
        } else {
            entity.tRot += 0.02F;
            entity.nextPageTurningSpeed -= 0.1F;
        }

        while (entity.nextPageAngle >= (float) Math.PI) {
            entity.nextPageAngle -= ((float) Math.PI * 2F);
        }

        while (entity.nextPageAngle < -(float) Math.PI) {
            entity.nextPageAngle += ((float) Math.PI * 2F);
        }

        while (entity.tRot >= (float) Math.PI) {
            entity.tRot -= ((float) Math.PI * 2F);
        }

        while (entity.tRot < -(float) Math.PI) {
            entity.tRot += ((float) Math.PI * 2F);
        }

        float f2 = entity.tRot - entity.nextPageAngle;
        while (f2 >= (float) Math.PI) {
            f2 -= ((float) Math.PI * 2F);
        }

        while (f2 < -(float) Math.PI) {
            f2 += ((float) Math.PI * 2F);
        }

        entity.nextPageAngle += f2 * 0.4F;
        entity.nextPageTurningSpeed = Mth.clamp(entity.nextPageTurningSpeed, 0.0F, 1.0F);
        ++entity.ticks;
        entity.oFlip = entity.flip;
        float f = (entity.flipT - entity.flip) * 0.4F;
        float f3 = 0.2F;
        f = Mth.clamp(f, -0.2F, 0.2F);
        entity.flipA += (f - entity.flipA) * 0.9F;
        entity.flip += entity.flipA;
    }

    public int getTicks() {
        return ticks;
    }

    /**
     * Consume 1 enchantment instance
     *
     * @param enchInst The enchantment to consume
     * @return Whether the given enchantment existed and was consumed
     */
    public boolean consumeEnchantment(EnchantmentInstanceMod enchInst) {
        int count = enchantmentMaps.getOrDefault(enchInst, 0);
        if (count <= 0) {
            return false;
        } else if (count == 1) {
            enchantmentMaps.removeInt(enchInst);
        } else {
            enchantmentMaps.put(enchInst, count - 1);
        }
        notifyListeners();
        return true;
    }

    /**
     * Add one enchantment instance
     */
    public void addEnchantment(EnchantmentInstanceMod enchInst) {
        this.addEnchantment(enchInst, 1);
    }

    public void registerListener(IEnchantmentListener listener) {
        this.listeners.put(listener, listener);
    }

    /**
     * @return Whether the given enchantment is present
     */
    public boolean hasEnchantment(EnchantmentInstanceMod enchInst) {
        return enchantmentMaps.getOrDefault(enchInst, 0) > 0;
    }

    /**
     * @return Unmofifiable map of all enchantment instances and their count ({@code >0})
     */
    public Object2IntMap<EnchantmentInstanceMod> getEnchantments() {
        return Object2IntMaps.unmodifiable(enchantmentMaps);
    }

    public int getEnchantmentCount() {
        return enchantmentMaps.values().stream().mapToInt(s -> s).sum();
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        enchantmentMaps.clear();
        nbt.getList("enchantments", 10).forEach(i -> {
            CompoundTag entry = (CompoundTag) i;
            try {
                ResourceLocation eID = new ResourceLocation(entry.getString("id"));
                int level = entry.getInt("level");
                int count = entry.getInt("count");
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(eID);
                if (enchantment == null) {
                    LOGGER.info("Cannot find stored enchantment {} in registry", eID);
                } else {
                    EnchantmentInstanceMod inst = new EnchantmentInstanceMod(enchantment, level);
                    if (enchantmentMaps.containsKey(inst)) {
                        LOGGER.warn("Multiple entries of the same enchantment instance in NBT");
                    }
                    enchantmentMaps.put(inst, count);
                }
            } catch (NullPointerException | ResourceLocationException e) {
                LOGGER.error("Illegal enchantment id in NBT {} {}", entry.getString("id"), e);
            }
        });
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        compound = super.save(compound);

        writeEnchantments(compound);

        return compound;
    }

    public void writeEnchantments(CompoundTag compound) {
        ListTag enchantments = new ListTag();
        enchantmentMaps.forEach((inst, count) -> {
            CompoundTag enchantment = new CompoundTag();
            //noinspection ConstantConditions
            enchantment.putString("id", inst.getEnchantment().getRegistryName().toString());
            enchantment.putInt("level", inst.getLevel());
            enchantment.putInt("count", count);
            enchantments.add(enchantment);
        });

        compound.put("enchantments", enchantments);
    }

    @Override
    public Optional<StorageTileEntity> getConnectedEnchantmentTE() {
        return Optional.of(this);
    }

    @Override
    public boolean hasConnectedTE() {
        return true;
    }

    public void removeListener(IEnchantmentListener listener) {
        this.listeners.remove(listener);
    }

    private void notifyListeners() {
        Object2IntMap<EnchantmentInstanceMod> list = getEnchantments();
        listeners.forEach((k, v) -> v.onEnchantmentsChanged(list));
    }

    public interface IEnchantmentListener {
        void onEnchantmentsChanged(Object2IntMap<EnchantmentInstanceMod> updatedList);
    }
}
