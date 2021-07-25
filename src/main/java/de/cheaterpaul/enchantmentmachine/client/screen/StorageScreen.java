package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class StorageScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchantment.png");
    private final int xSize = 197;
    private final int ySize = 222;
    private Object2IntMap<EnchantmentInstanceMod> enchantments = new Object2IntArrayMap<>();
    private ScrollableListButton<Pair<EnchantmentInstanceMod, Integer>> list;
    private int guiLeft;
    private int guiTop;

    public StorageScreen() {
        super(new TextComponent("Enchantments"));
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
        this.addWidget(list = new ScrollableListButton<>(this.guiLeft + 10, this.guiTop + 10, this.xSize - 20, this.ySize - 20, 21, EnchantmentItem::new));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.minecraft.getTextureManager().bindForSetup(BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstanceMod> enchantments) {
        this.enchantments = enchantments;
        this.list.setItems(enchantments.object2IntEntrySet().stream().map(s -> Pair.of(s.getKey(), s.getIntValue())).collect(Collectors.toSet()));
    }

    private class EnchantmentItem extends ScrollableListButton.ListItem<Pair<EnchantmentInstanceMod, Integer>> {

        private final ItemStack bookStack;
        private final Component name;

        public EnchantmentItem(Pair<EnchantmentInstanceMod, Integer> item) {
            super(item);
            bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(), item.getKey().getLevel()), bookStack);
            name = ((MutableComponent) item.getKey().getEnchantment().getFullname(item.getKey().getLevel())).withStyle(style -> style.getColor().getValue() == ChatFormatting.GRAY.getColor() ? style.applyFormat(ChatFormatting.WHITE) : style);
        }

        @Override
        public void render(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);
            StorageScreen.this.itemRenderer.renderAndDecorateFakeItem(bookStack, x + 5, y + 2 + yOffset);
            StorageScreen.this.font.drawShadow(matrixStack, name.getString(), x + 25, y + yOffset + 5, name.getStyle().getColor().getValue());


            String count = String.valueOf(bookStack.getCount());

            StorageScreen.this.font.drawShadow(matrixStack, count, x + listWidth - 10, y + yOffset + 5, 0xffffff);
        }

        @Override
        public void renderToolTip(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            if (mouseX > x && mouseX < x + listWidth && mouseY > y && mouseY < y + ySize) {
                StorageScreen.this.renderTooltip(matrixStack, bookStack, mouseX, mouseY);
            }
        }
    }
}
