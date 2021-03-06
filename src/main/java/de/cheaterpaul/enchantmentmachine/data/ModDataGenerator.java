package de.cheaterpaul.enchantmentmachine.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.cheaterpaul.enchantmentmachine.core.ModData;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModDataGenerator {

    public static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeClient()) {
            generator.addProvider(new BlockStateGenerator(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemModelGenerator(generator, event.getExistingFileHelper()));
        }
        generator.addProvider(new ModLootTableProvider(generator));
        generator.addProvider(new RecipeGenerator(generator));
    }

    public static class ItemModelGenerator extends ItemModelProvider {

        public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, REFERENCE.MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            getBuilder(ModData.enchanter_block.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.enchanter_block.getRegistryName().getPath()));
            getBuilder(ModData.disenchanter_block.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.disenchanter_block.getRegistryName().getPath()));
            getBuilder(ModData.storage_block.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(REFERENCE.MODID + ":block/" + ModData.storage_block.getRegistryName().getPath()));
        }
    }

    public static class BlockStateGenerator extends BlockStateProvider {
        public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
            super(gen, REFERENCE.MODID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            ModelFile enchanter = new ModelFile.ExistingModelFile(blockTexture(ModData.enchanter_block), models().existingFileHelper);

            ModelFile enchantment_block = models().withExistingParent(ModData.storage_block.getRegistryName().toString(), "block/enchanting_table")
                    .texture("particle", new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"))
                    .texture("top", new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_top"))
                    .texture("side", new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_side"))
                    .texture("bottom", new ResourceLocation(REFERENCE.MODID, "block/enchanting_table_bottom"));

            ModelFile disenchanter = new ModelFile.ExistingModelFile(blockTexture(ModData.disenchanter_block), models().existingFileHelper);

            simpleBlock(ModData.enchanter_block, enchanter);
            simpleBlock(ModData.disenchanter_block, disenchanter);
            simpleBlock(ModData.storage_block, enchantment_block);
        }
    }

    public static class RecipeGenerator extends RecipeProvider {
        public RecipeGenerator(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
            ShapedRecipeBuilder.shaped(ModData.storage_block).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND).pattern("BBB").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
            ShapedRecipeBuilder.shaped(ModData.disenchanter_block).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND_AXE).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
            ShapedRecipeBuilder.shaped(ModData.enchanter_block).define('B', Items.BOOK).define('#', Blocks.CRYING_OBSIDIAN).define('D', Items.DIAMOND).pattern(" B ").pattern("D#D").pattern("###").unlockedBy("has_obsidian", has(Blocks.CRYING_OBSIDIAN)).save(consumer);
        }
    }

    private static class ModLootTableProvider extends LootTableProvider {

        public ModLootTableProvider(DataGenerator dataGeneratorIn) {
            super(dataGeneratorIn);
        }

        @Nonnull
        @Override
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
            return ImmutableList.of(Pair.of(Tables::new, LootParameterSets.BLOCK));
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, @Nonnull ValidationTracker validationtracker) {
            map.forEach((resourceLocation, lootTable) -> LootTableManager.validate(validationtracker, resourceLocation, lootTable));
        }

        private static class Tables extends BlockLootTables {
            @Override
            protected void addTables() {
                this.dropSelf(ModData.disenchanter_block);
                this.dropSelf(ModData.enchanter_block);
                this.dropSelf(ModData.storage_block);
            }

            @Nonnull
            @Override
            protected Iterable<Block> getKnownBlocks() {
                return Lists.newArrayList(ModData.disenchanter_block, ModData.enchanter_block, ModData.storage_block);
            }
        }
    }
}
