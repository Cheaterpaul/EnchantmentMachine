package de.cheaterpaul.enchantmentmachine.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.client.gui.screens.StorageScreen;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.MultilineTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.BiFunction;

import static net.minecraft.client.gui.screens.Screen.getTooltipFromItem;

public class EnchantmentItem extends SimpleList.Entry<EnchantmentItem> {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");

    private final ItemStack bookStack;

    public EnchantmentItem(Pair<EnchantmentInstanceMod, Integer> item) {
        super(makeWhite(item.getKey().getEnchantmentName()), () -> {});
        this.bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
        EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(), item.getKey().getLevel()), bookStack);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
        guiGraphics.blitWithBorder(WIDGETS_LOCATION, pLeft, pTop, 0, 46 + 21, pWidth, pHeight +5, 200, 18, 2, 3, 2, 2);

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        RenderSystem.applyModelViewMatrix();
        guiGraphics.renderItem(bookStack, pLeft+5, pTop +1);
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        guiGraphics.drawString(font, this.component, pLeft + 25, pTop + 5, -1);

        String count = String.valueOf(bookStack.getCount());

        guiGraphics.drawString(font, count, pLeft + pWidth - 10, pTop + 5, 0xffffff);

        if (pIsMouseOver) {
            minecraft.screen.setTooltipForNextRenderPass(getTooltipFromItem(Minecraft.getInstance(), this.bookStack).stream().flatMap(a -> Tooltip.splitTooltip(minecraft, a).stream()).toList());
        }
    }

    private static Component makeWhite(Component source) {
        Style style = source.getStyle();
        //noinspection ConstantConditions
        if(style.getColor() == null || style.getColor().getValue() == ChatFormatting.GRAY.getColor()) {
            source = source.plainCopy().withStyle(style.withColor(ChatFormatting.WHITE));
        }
        return source;
    }
}
