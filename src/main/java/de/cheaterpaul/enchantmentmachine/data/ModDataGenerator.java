package de.cheaterpaul.enchantmentmachine.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModDataGenerator {

    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        generator.addProvider(event.includeClient(), new BlockStateGenerator(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new RecipeGenerator(packOutput, event.getLookupProvider()));
        generator.addProvider(event.includeServer(), new ModBlockTagsProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper()));
    }

    public static class ItemModelGenerator extends ItemModelProvider {

        public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            getBuilder(ModData.enchanter_block.getId().toString()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.enchanter_block.getId().getPath()));
            getBuilder(ModData.disenchanter_block.getId().toString()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.disenchanter_block.getId().getPath()));
            getBuilder(ModData.storage_block.getId().toString()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.storage_block.getId().getPath()));
        }
    }

    public static class BlockStateGenerator extends BlockStateProvider {
        public BlockStateGenerator(PackOutput packOutput, ExistingFileHelper exFileHelper) {
            super(packOutput, REFERENCE.MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            ModelFile enchanter = new ModelFile.ExistingModelFile(blockTexture(ModData.enchanter_block.get()), models().existingFileHelper);

            ModelFile enchantment_block = models().withExistingParent(BuiltInRegistries.BLOCK.getKey(ModData.storage_block.get()).toString(), "block/enchanting_table")
                    .texture("particle", ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "block/enchanting_table_bottom"))
                    .texture("top", ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "block/enchanting_table_top"))
                    .texture("side", ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "block/enchanting_table_side"))
                    .texture("bottom", ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "block/enchanting_table_bottom")).renderType(ResourceLocation.withDefaultNamespace("cutout"));

            ModelFile disenchanter = new ModelFile.ExistingModelFile(blockTexture(ModData.disenchanter_block.get()), models().existingFileHelper);

            simpleBlock(ModData.enchanter_block.get(), enchanter);
            simpleBlock(ModData.disenchanter_block.get(), disenchanter);
            simpleBlock(ModData.storage_block.get(), enchantment_block);
        }
    }

    public static class RecipeGenerator extends RecipeProvider {
        public RecipeGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
            super(packOutput, pRegistries);
        }

        @Override
        protected void buildRecipes(@Nonnull RecipeOutput consumer) {
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModData.storage_block.get()).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND).pattern("BBB").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModData.disenchanter_block.get()).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND_AXE).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModData.enchanter_block.get()).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
        }

    }

    private static class ModLootTableProvider extends LootTableProvider {

        public ModLootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
            super(packOutput, Collections.emptySet(), ImmutableList.of(new SubProviderEntry(Tables::new, LootContextParamSets.BLOCK)), pRegistries);
        }

        private static class Tables extends BlockLootSubProvider {

            protected Tables(HolderLookup.Provider provider) {
                super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), provider);
            }

            @Override
            protected void generate() {
                this.dropSelf(ModData.disenchanter_block.get());
                this.dropSelf(ModData.enchanter_block.get());
                this.dropSelf(ModData.storage_block.get());
            }

            @Nonnull
            @Override
            protected Iterable<Block> getKnownBlocks() {
                return Lists.newArrayList(ModData.disenchanter_block.get(), ModData.enchanter_block.get(), ModData.storage_block.get());
            }
        }
    }

    public static class ModBlockTagsProvider extends BlockTagsProvider {

        public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider holderLookup) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModData.disenchanter_block.get(), ModData.enchanter_block.get(), ModData.storage_block.get());
        }
    }
}
