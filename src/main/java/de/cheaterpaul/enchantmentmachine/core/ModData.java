package de.cheaterpaul.enchantmentmachine.core;

import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.block.DisenchanterBlock;
import de.cheaterpaul.enchantmentmachine.block.EnchanterBlock;
import de.cheaterpaul.enchantmentmachine.block.StorageBlock;
import de.cheaterpaul.enchantmentmachine.block.entity.DisenchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.block.entity.EnchanterBlockEntity;
import de.cheaterpaul.enchantmentmachine.block.entity.StorageBlockEntity;
import de.cheaterpaul.enchantmentmachine.inventory.DisenchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModData {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, REFERENCE.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, REFERENCE.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, REFERENCE.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, REFERENCE.MODID);

    public static final RegistryObject<EnchanterBlock> enchanter_block = registerItemBlock("enchanter_block", () ->new EnchanterBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F)), new Item.Properties().tab(EnchantmentMachineMod.CREATIVE_TAB));
    public static final RegistryObject<DisenchanterBlock> disenchanter_block = registerItemBlock("disenchanter_block", () ->new DisenchanterBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F)), new Item.Properties().tab(EnchantmentMachineMod.CREATIVE_TAB));
    public static final RegistryObject<StorageBlock> storage_block = registerItemBlock("enchantment_block",() ->new StorageBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).noOcclusion()), new Item.Properties().tab(EnchantmentMachineMod.CREATIVE_TAB).stacksTo(1).rarity(Rarity.EPIC) );
    public static final RegistryObject<BlockEntityType<EnchanterBlockEntity>> enchanter_tile = BLOCK_ENTITIES.register("enchanter_tile", () ->BlockEntityType.Builder.of(EnchanterBlockEntity::new, enchanter_block.get()).build(null));
    public static final RegistryObject<BlockEntityType<DisenchanterBlockEntity>> disenchanter_tile = BLOCK_ENTITIES.register("disenchanter_tile", () ->BlockEntityType.Builder.of(DisenchanterBlockEntity::new, disenchanter_block.get()).build(null) );
    public static final RegistryObject<BlockEntityType<StorageBlockEntity>> storage_tile = BLOCK_ENTITIES.register("storage_tile", () ->BlockEntityType.Builder.of(StorageBlockEntity::new, storage_block.get()).build(null));
    public static final RegistryObject<MenuType<EnchanterContainerMenu>> enchanter_container = MENU_TYPES.register("enchanter_container", () -> new MenuType<>(EnchanterContainerMenu::new));
    public static final RegistryObject<MenuType<DisenchanterContainerMenu>> disenchanter_container = MENU_TYPES.register("disenchanter_container",() -> new MenuType<>(DisenchanterContainerMenu::new));


    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENU_TYPES.register(bus);
    }

    static <T extends Block> RegistryObject<T> registerItemBlock(String name, Supplier<T> block, Item.Properties properties) {
        RegistryObject<T> blockreg = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(blockreg.get(), properties));
        return blockreg;
    }

}
