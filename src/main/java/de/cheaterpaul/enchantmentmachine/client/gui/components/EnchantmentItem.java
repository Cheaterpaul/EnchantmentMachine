package de.cheaterpaul.enchantmentmachine.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static net.minecraft.client.gui.screens.Screen.getTooltipFromItem;

public class EnchantmentItem extends SimpleList.Entry<EnchantmentItem> {
    public static final WidgetSprites WIDGETS_LOCATION = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_highlighted"));

    private final ItemStack bookStack;

    public EnchantmentItem(Pair<EnchantmentInstanceMod, Integer> item) {
        super(makeWhite(item.getKey().getEnchantmentName()), () -> {});
        this.bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(item.getKey().enchantment(), item.getKey().level());
        EnchantmentHelper.setEnchantments(bookStack, mutable.toImmutable());
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
        guiGraphics.blitSprite(WIDGETS_LOCATION.get(true, false), pLeft, pTop, pWidth, pHeight +4 );

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        PoseStack modelViewStack = guiGraphics.pose();
        modelViewStack.pushPose();
        RenderSystem.applyModelViewMatrix();
        guiGraphics.renderItem(bookStack, pLeft+5, pTop +1);
        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        guiGraphics.drawString(font, this.getNarration(), pLeft + 25, pTop + 5, -1);

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
