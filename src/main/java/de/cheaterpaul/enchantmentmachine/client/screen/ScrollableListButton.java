package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstance;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * add this buttons first and render them last and call {@link #mouseDragged(double, double, int, double, double)}
 */
public class ScrollableListButton<T> extends ExtendedButton {

    private static final ResourceLocation MISC = new ResourceLocation(REFERENCE.MODID, "textures/gui/misc.png");

    private final ItemCreator<T> itemSupplier;
    private final int itemHeight;
    private final int scrollerWidth = 9;

    private final List<ListItem<T>> listItems = new ArrayList<>();
    private int scrolled;
    private double scrolledD;
    private boolean scrollerClicked;


    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemHeight) {
        this(xPos, yPos, width, height, itemHeight, ListItem::new);
    }
    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemHeight, ItemCreator<T> itemSupplier) {
        super(xPos, yPos, width, height, new StringTextComponent(""), (button) ->{});
        this.itemHeight = itemHeight;
        this.itemSupplier = itemSupplier;
    }

    public void setItems(Set<T> elements) {
        this.listItems.clear();
        elements.forEach(item -> this.listItems.add(this.itemSupplier.apply(item)));
    }

    public void addItem(T element) {
        this.listItems.add(this.itemSupplier.apply(element));
    }

    public void removeItem(T element) {
        this.listItems.removeIf(item -> item.item == element);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int itemHeight = this.itemHeight-1; // only 1 pixel between items
        for (int i = 0; i < this.listItems.size(); i++) {

            int y = i*itemHeight - scrolled;

            if (y < -itemHeight) {
                continue;
            }


            ListItem<T> item = this.listItems.get(i);
            item.render(matrixStack, this.x, this.y, this.width  - scrollerWidth, this.height, this.itemHeight, y,  mouseX, mouseY, partialTicks, this.getBlitOffset());

        }
        this.renderScrollBar(matrixStack, mouseX, mouseY, partialTicks);
        if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height) {
            this.renderToolTip(matrixStack, mouseX, mouseY);
        }
    }

    private void renderScrollBar(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        GuiUtils.drawContinuousTexturedBox(matrixStack, MISC, this.x + this.width - this.scrollerWidth, this.y,0,0,9, this.height, 9, 200,2, getBlitOffset());
        this.renderScroller(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderScroller(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int scrollerHeight = 27;
        int scrollHeight = this.height - 2 - scrollerHeight;
        float perc = (float)this.scrolled/(float)(this.listItems.size() * this.itemHeight - this.height);
        int yOffset = (int)(scrollHeight * perc);
        Minecraft.getInstance().textureManager.bindTexture(MISC);
        blit(matrixStack, this.x + this.width - this.scrollerWidth + 1, this.y + yOffset+ 1, 9,0,7,27);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scrolled = Math.max(this.scrolled +  4 *((int) delta), 0);
        this.scrolled = Math.min(this.scrolled, this.listItems.size() * this.itemHeight - this.height);
        this.scrolledD = scrolled;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(scrollerClicked) {
            this.scrolledD += dragY * 1.5;
            this.scrolled = ((int) this.scrolledD);
            this.scrolled = Math.max(this.scrolled, 0);
            this.scrolled = Math.min(this.scrolled, this.listItems.size() * this.itemHeight - this.height);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolledD = this.scrolled;
        if (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height) {
            if (mouseX >= this.x + this.width - this.scrollerWidth) {
                this.scrollerClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        int itemHeight = this.itemHeight-1; // only 1 pixel between items
        for (int i = 0; i < this.listItems.size(); i++) {

            int y = i*itemHeight - scrolled;

            if (y < -itemHeight) {
                continue;
            }

            ListItem<T> item = this.listItems.get(i);
            if (i != 10){
                continue;
            }
            item.renderToolTip(matrixStack, this.x, this.y + y, this.width  - scrollerWidth, this.height, this.itemHeight, y,  mouseX, mouseY, this.getBlitOffset());

        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrollerClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @FunctionalInterface
    public interface ItemCreator<T> {
        ListItem<T> apply(T item);
    }

    public static class ListItem<T> {

        private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

        private final T item;

        public ListItem(T item) {
            this.item = item;
        }

        public void render(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            int ySize = MathHelper.clamp(listHeight - yOffset,0,itemHeight);
            int yTopBorder = 3;
            int yBottomBorder = 3;
            int v = 66;
            int textureHeight= 20;
            if (ySize <= 0)return;
            if (ySize < itemHeight) {
                if (ySize < 3) {
                    yTopBorder = ySize;
                }
                yBottomBorder = 0;
                textureHeight-=3;
            }
            if (yOffset < 0) {
                ySize+=yOffset;
                yTopBorder = Math.max(0, yTopBorder + yOffset);
                v += 3-yTopBorder;
                textureHeight -=3-yTopBorder;
                if (-yOffset >= itemHeight-2) {
                    yBottomBorder = 1;
                }
                yOffset=0;
            }
            renderBox(matrixStack, WIDGETS, x, y + yOffset,0,v, listWidth+1, ySize, 200, textureHeight, yTopBorder, yBottomBorder,3,3, zLevel);
        }

        public void renderBox(MatrixStack matrixStack, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float partialTicks) {
            GuiUtils.drawContinuousTexturedBox(matrixStack, texture, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, partialTicks);
        }

        public void renderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            int ySize = MathHelper.clamp(listHeight - yOffset,0,itemHeight);

            Screen screen = Minecraft.getInstance().currentScreen;;
            if (mouseX > x && mouseX < x + listWidth && mouseY> y && mouseY < y + ySize) {
                screen.renderTooltip(matrixStack, new StringTextComponent("test"), mouseX, mouseY);
            }
        }
    }
}
