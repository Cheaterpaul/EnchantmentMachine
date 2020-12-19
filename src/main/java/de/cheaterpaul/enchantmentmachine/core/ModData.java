package de.cheaterpaul.enchantmentmachine.core;

import de.cheaterpaul.enchantmentmachine.block.EnchantmentMachineBlock;
import de.cheaterpaul.enchantmentmachine.inventory.EnchantmentMachineContainer;
import de.cheaterpaul.enchantmentmachine.item.EnchantmentMachineItem;
import de.cheaterpaul.enchantmentmachine.tiles.EnchantmentMachineTileEntity;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModData {

    public static final EnchantmentMachineBlock enchantment_machine_block;
    public static final EnchantmentMachineItem enchantment_machine_item;
    public static final TileEntityType<EnchantmentMachineTileEntity> enchantment_machine_tile;
    public static final ContainerType<EnchantmentMachineContainer> enchantment_machine_container;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(enchantment_machine_block);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(enchantment_machine_item);
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(enchantment_machine_tile);
    }

    @SubscribeEvent
    public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(enchantment_machine_container);
    }

    static {
        (enchantment_machine_block = new EnchantmentMachineBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON))).setRegistryName(REFERENCE.MODID, "enchantment_machine");
        (enchantment_machine_item = new EnchantmentMachineItem(new Item.Properties())).setRegistryName(REFERENCE.MODID, "enchantment_machine");
        (enchantment_machine_tile = TileEntityType.Builder.create(EnchantmentMachineTileEntity::new, enchantment_machine_block).build(null)).setRegistryName(REFERENCE.MODID, "enchantment_machine");
        (enchantment_machine_container = new ContainerType<>(EnchantmentMachineContainer::new)).setRegistryName(REFERENCE.MODID, "enchantment_machine");
    }
}
