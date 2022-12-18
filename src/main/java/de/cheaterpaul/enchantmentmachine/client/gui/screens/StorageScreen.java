package de.cheaterpaul.enchantmentmachine.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.client.gui.components.ScrollWidget;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.MultilineTooltip;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
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
import net.minecraftforge.client.gui.ScreenUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
public class StorageScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchantment.png");
    private final int xSize = 197;
    private final int ySize = 222;
    private Object2IntMap<EnchantmentInstanceMod> enchantments = new Object2IntArrayMap<>();
    private ScrollWidget<Pair<EnchantmentInstanceMod, Integer>> list;
    private int guiLeft;
    private int guiTop;

    public StorageScreen() {
        super(Component.literal("Enchantments"));
    }

    @Override
    public void resize(@Nonnull Minecraft minecraft, int p_231152_2_, int p_231152_3_) {
        super.resize(minecraft, p_231152_2_, p_231152_3_);
        this.updateEnchantments(enchantments);
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.addRenderableWidget(this.list = new ScrollWidget<>(this.guiLeft + 10, this.guiTop + 10, this.xSize - 25, this.ySize - 20));
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
        this.list.updateContent(EnchantmentItem::new, builder -> {
            if (this.enchantments != null) {
                for (Map.Entry<EnchantmentInstanceMod, Integer> entry : this.enchantments.object2IntEntrySet()) {
                    builder.addWidget(Pair.of(entry.getKey(), entry.getValue()));
                }
            }
        });
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    private class EnchantmentItem extends AbstractWidget {

        private final ItemStack bookStack;
        private final Component name;
        private final BiFunction<Integer, Integer, Boolean> isXYInBounds;

        public EnchantmentItem(Pair<EnchantmentInstanceMod, Integer> item, int x, int y, int width, BiFunction<Integer, Integer, Boolean> isXYInBounds) {
            super(x, y, width, 18, item.getKey().getEnchantmentName());
            this.bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
            this.isXYInBounds = isXYInBounds;
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(), item.getKey().getLevel()), bookStack);
            this.name = item.getKey().getEnchantment().getFullname(item.getKey().getLevel());
            Style style = this.name.getStyle();
            //noinspection ConstantConditions
            if(style.getColor() == null || style.getColor().getValue() == ChatFormatting.GRAY.getColor()) {
                ((MutableComponent) this.name).withStyle(style.withColor(ChatFormatting.WHITE));
            }
        }

        @Override
        public void renderButton(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            this.isHovered = this.isHovered && this.isXYInBounds.apply(pMouseX, pMouseY);
            ScreenUtils.blitWithBorder(pPoseStack, WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46 + 21, this.width, this.height, 200, 18, 2, 3, 2, 2, this.getBlitOffset());
            this.renderBg(pPoseStack, Minecraft.getInstance(), pMouseX, pMouseY);

            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;

            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.translate(0,-StorageScreen.this.list.getScrollAmount(),0);
            RenderSystem.applyModelViewMatrix();
            StorageScreen.this.itemRenderer.renderAndDecorateFakeItem(bookStack, this.getX()+5, this.getY() +1);
            modelViewStack.popPose();
            RenderSystem.applyModelViewMatrix();
            drawString(pPoseStack, font, name, this.getX() + 25, this.getY() + 5, -1);

            String count = String.valueOf(bookStack.getCount());

            drawString(pPoseStack, font, count, this.getX() + this.width - 10, this.getY() + 5, 0xffffff);

            if (this.isHovered) {
                this.setTooltip(new MultilineTooltip(StorageScreen.this.getTooltipFromItem(this.bookStack)));
            }
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }

    }
}
