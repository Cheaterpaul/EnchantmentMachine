package de.cheaterpaul.enchantmentmachine.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.client.gui.components.EnchantmentItem;
import de.cheaterpaul.enchantmentmachine.client.gui.components.SimpleList;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.MultilineTooltip;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
    private SimpleList<EnchantmentItem> list;
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
        this.list = SimpleList.<EnchantmentItem>builder(this.guiLeft + 10, this.guiTop + 10, this.xSize - 25, this.ySize - 20).build();
        this.addRenderableWidget(this.list);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        int i = this.guiLeft;
        int j = this.guiTop;
        guiGraphics.blit(BACKGROUND, i, j, 0, 0, this.xSize, this.ySize);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstanceMod> enchantments) {
        this.enchantments = enchantments;
        this.list.replace(this.enchantments.object2IntEntrySet().stream().map(entry -> new EnchantmentItem(Pair.of(entry.getKey(), entry.getValue()))).toList());
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

}
