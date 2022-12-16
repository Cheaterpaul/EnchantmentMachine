package de.cheaterpaul.enchantmentmachine.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.client.gui.components.ScrollableList;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.MultilineTooltip;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class StorageScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchantment.png");
    private final int xSize = 197;
    private final int ySize = 222;
    private Object2IntMap<EnchantmentInstanceMod> enchantments = new Object2IntArrayMap<>();
    private ScrollableList<Pair<EnchantmentInstanceMod, Integer>> list;
    private int guiLeft;
    private int guiTop;

    public StorageScreen() {
        super(Component.literal("Enchantments"));
    }

    @Override
    public void resize(@Nonnull Minecraft minecraft, int p_231152_2_, int p_231152_3_) {
        int scrolled = this.list.getScrolled();
        super.resize(minecraft, p_231152_2_, p_231152_3_);
        this.updateEnchantments(enchantments);
        this.list.setScrolled(scrolled);
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.addRenderableWidget(list = new ScrollableList<>(this.guiLeft + 10, this.guiTop + 10, this.xSize - 20, this.ySize - 20, 21, EnchantmentItem::new));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstanceMod> enchantments) {
        this.enchantments = enchantments;
        this.list.setItems(enchantments.object2IntEntrySet().stream().map(s -> Pair.of(s.getKey(), s.getIntValue())).sorted(Comparator.comparing(o -> o.getKey().getEnchantmentName())).collect(Collectors.toList()));
    }

    private class EnchantmentItem extends ScrollableList.ListItem<Pair<EnchantmentInstanceMod, Integer>> {

        private final ItemStack bookStack;
        private final Component name;

        public EnchantmentItem(int width, int height, Pair<EnchantmentInstanceMod, Integer> item) {
            super(width, height, item);
            this.bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(), item.getKey().getLevel()), bookStack);
            this.name = item.getKey().getEnchantment().getFullname(item.getKey().getLevel());
            Style style = this.name.getStyle();
            //noinspection ConstantConditions
            if(style.getColor() == null || style.getColor().getValue() == ChatFormatting.GRAY.getColor()) {
                ((MutableComponent) this.name).withStyle(style.withColor(ChatFormatting.WHITE));
            }
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
            StorageScreen.this.itemRenderer.renderAndDecorateFakeItem(bookStack, 5, 2);
            StorageScreen.this.font.drawShadow(pPoseStack, name, 25, 5, -1);


            String count = String.valueOf(bookStack.getCount());

            StorageScreen.this.font.drawShadow(pPoseStack, count, this.width - 10, 5, 0xffffff);

            if (this.isHovered) {
                this.setTooltip(new MultilineTooltip(StorageScreen.this.getTooltipFromItem(this.bookStack)));
            }
        }

    }
}
