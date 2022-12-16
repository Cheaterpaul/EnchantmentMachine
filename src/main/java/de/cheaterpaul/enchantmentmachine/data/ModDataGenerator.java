package de.cheaterpaul.enchantmentmachine.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
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
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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
        generator.addProvider(event.includeClient(), new BlockStateGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new ModLootTableProvider(packOutput));
        generator.addProvider(event.includeServer(), new RecipeGenerator(packOutput));
        generator.addProvider(event.includeServer(), new ModBlockTagsProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper()));
    }

    public static class ItemModelGenerator extends ItemModelProvider {

        public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            getBuilder(ModData.enchanter_block.getId().toString()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.enchanter_block.getId().getPath()));
            getBuilder(ModData.disenchanter_block.getId().toString()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.disenchanter_block.getId().getPath()));
            getBuilder(ModData.storage_block.getId().toString()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.storage_block.getId().getPath()));
        }
    }

    public static class BlockStateGenerator extends BlockStateProvider {
        public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
            super(gen, REFERENCE.MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            ModelFile enchanter = new ModelFile.ExistingModelFile(blockTexture(ModData.enchanter_block.get()), models().existingFileHelper);

            ModelFile enchantment_block = models().withExistingParent(ForgeRegistries.BLOCKS.getKey(ModData.storage_block.get()).toString(), "block/enchanting_table")
                    .texture("particle", new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"))
                    .texture("top", new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_top"))
                    .texture("side", new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_side"))
                    .texture("bottom", new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom")).renderType(new ResourceLocation("cutout"));

            ModelFile disenchanter = new ModelFile.ExistingModelFile(blockTexture(ModData.disenchanter_block.get()), models().existingFileHelper);

            simpleBlock(ModData.enchanter_block.get(), enchanter);
            simpleBlock(ModData.disenchanter_block.get(), disenchanter);
            simpleBlock(ModData.storage_block.get(), enchantment_block);
        }
    }

    public static class RecipeGenerator extends RecipeProvider {
        public RecipeGenerator(PackOutput packOutput) {
            super(packOutput);
        }

        @Override
        protected void buildRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModData.storage_block.get()).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND).pattern("BBB").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModData.disenchanter_block.get()).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND_AXE).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModData.enchanter_block.get()).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
        }

    }

    private static class ModLootTableProvider extends LootTableProvider {

        public ModLootTableProvider(PackOutput packOutput) {
            super(packOutput, Collections.emptySet(), ImmutableList.of(new SubProviderEntry(Tables::new, LootContextParamSets.BLOCK)));
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, @Nonnull ValidationContext validationContext) {
            map.forEach((resourceLocation, lootTable) -> LootTables.validate(validationContext, resourceLocation, lootTable));
        }

        private static class Tables extends BlockLootSubProvider {

            protected Tables() {
                super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
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
