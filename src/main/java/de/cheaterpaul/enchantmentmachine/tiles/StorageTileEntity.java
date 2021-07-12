package de.cheaterpaul.enchantmentmachine.tiles;

import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Random;
import java.util.WeakHashMap;

public class StorageTileEntity extends TileEntity implements IEnchantmentMachine, ITickableTileEntity {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ITextComponent name = Utils.genTranslation("tile", "enchantment.name");
    private static final Random random = new Random();
    /**
     * Used as list, just using map because it implements all the required weak reference stuff
     */
    private final WeakHashMap<IEnchantmentListener, IEnchantmentListener> listeners = new WeakHashMap<>();
    private final Object2IntArrayMap<EnchantmentInstance> enchantmentMaps = new Object2IntArrayMap<>();

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

    public StorageTileEntity() {
        super(ModData.storage_tile);
    }

    public void addEnchantment(EnchantmentInstance enchInst, int count) {
        int c = enchantmentMaps.getOrDefault(enchInst, 0);
        enchantmentMaps.put(enchInst, c + count);
        notifyListeners();
    }

    @Override
    public void tick() {
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.pageAngle = this.nextPageAngle;
        PlayerEntity playerentity = this.level.getNearestPlayer((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D, 3.0D, false);
        if (playerentity != null) {
            double d0 = playerentity.getX() - ((double) this.worldPosition.getX() + 0.5D);
            double d1 = playerentity.getZ() - ((double) this.worldPosition.getZ() + 0.5D);
            this.tRot = (float) MathHelper.atan2(d1, d0);
            this.nextPageTurningSpeed += 0.1F;
            if (this.nextPageTurningSpeed < 0.5F || random.nextInt(40) == 0) {
                float f1 = this.flipT;

                do {
                    this.flipT += (float) (random.nextInt(4) - random.nextInt(4));
                } while (f1 == this.flipT);
            }
        } else {
            this.tRot += 0.02F;
            this.nextPageTurningSpeed -= 0.1F;
        }

        while (this.nextPageAngle >= (float) Math.PI) {
            this.nextPageAngle -= ((float) Math.PI * 2F);
        }

        while (this.nextPageAngle < -(float) Math.PI) {
            this.nextPageAngle += ((float) Math.PI * 2F);
        }

        while (this.tRot >= (float) Math.PI) {
            this.tRot -= ((float) Math.PI * 2F);
        }

        while (this.tRot < -(float) Math.PI) {
            this.tRot += ((float) Math.PI * 2F);
        }

        float f2;
        for (f2 = this.tRot - this.nextPageAngle; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F)) {
        }

        while (f2 < -(float) Math.PI) {
            f2 += ((float) Math.PI * 2F);
        }

        this.nextPageAngle += f2 * 0.4F;
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        ++this.ticks;
        this.oFlip = this.flip;
        float f = (this.flipT - this.flip) * 0.4F;
        float f3 = 0.2F;
        f = MathHelper.clamp(f, -0.2F, 0.2F);
        this.flipA += (f - this.flipA) * 0.9F;
        this.flip += this.flipA;
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
    public boolean consumeEnchantment(EnchantmentInstance enchInst) {
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
     *
     * @param enchInst
     */
    public void addEnchantment(EnchantmentInstance enchInst) {
        this.addEnchantment(enchInst, 1);
    }

    public void registerListener(IEnchantmentListener listener) {
        this.listeners.put(listener, listener);
    }

    /**
     * @param enchInst
     * @return Whether the given enchantment is present
     */
    public boolean hasEnchantment(EnchantmentInstance enchInst) {
        return enchantmentMaps.getOrDefault(enchInst, 0) > 0;
    }

    /**
     * @return Unmofifiable map of all enchantment instances and their count ({@code >0})
     */
    public Object2IntMap<EnchantmentInstance> getEnchantments() {
        return Object2IntMaps.unmodifiable(enchantmentMaps);
    }

    public int getEnchantmentCount() {
        return enchantmentMaps.values().stream().mapToInt(s -> s).sum();
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        enchantmentMaps.clear();
        nbt.getList("enchantments", 10).forEach(i -> {
            CompoundNBT entry = (CompoundNBT) i;
            try {
                ResourceLocation eID = new ResourceLocation(entry.getString("id"));
                int level = entry.getInt("level");
                int count = entry.getInt("count");
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(eID);
                if (enchantment == null) {
                    LOGGER.info("Cannot find stored enchantment {} in registry", eID);
                } else {
                    EnchantmentInstance inst = new EnchantmentInstance(enchantment, level);
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
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        compound = super.save(compound);

        writeEnchantments(compound);

        return compound;
    }

    public void writeEnchantments(CompoundNBT compound) {
        ListNBT enchantments = new ListNBT();
        enchantmentMaps.forEach((inst, count) -> {
            CompoundNBT enchantment = new CompoundNBT();
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
        Object2IntMap<EnchantmentInstance> list = getEnchantments();
        listeners.forEach((k, v) -> v.onEnchantmentsChanged(list));
    }

    public interface IEnchantmentListener {
        void onEnchantmentsChanged(Object2IntMap<EnchantmentInstance> updatedList);
    }
}
