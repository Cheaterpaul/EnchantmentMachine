package de.cheaterpaul.enchantmentmachine.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * make sure that {@link net.minecraft.client.gui.INestedGuiEventHandler#mouseDragged(double, double, int, double, double)} is being called
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
    private boolean canScroll = true;


    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemHeight) {
        this(xPos, yPos, width, height, itemHeight, ListItem::new);
    }
    public ScrollableListButton(int xPos, int yPos, int width, int height, int itemHeight, ItemCreator<T> itemSupplier) {
        super(xPos, yPos, width, height, new StringTextComponent(""), (button) ->{});
        this.itemHeight = itemHeight;
        this.itemSupplier = itemSupplier;
    }

    public void setItems(Collection<T> elements) {
        this.listItems.clear();
        elements.forEach(item -> this.listItems.add(this.itemSupplier.apply(item)));
        this.setScrolled(0);
        this.canScroll = this.listItems.size() * this.itemHeight > this.height;
    }

    public void addItem(T element) {
        this.listItems.add(this.itemSupplier.apply(element));
    }

    public void removeItem(T element) {
        this.listItems.removeIf(item -> item.item == element);
        if (this.scrolled > this.listItems.size() * this.itemHeight - this.height) {
            this.setScrolled(this.listItems.size() * this.itemHeight - this.height);
        }
    }

    private void setScrolled(int scrolled) {
        this.scrolledD = this.scrolled = scrolled;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        RenderSystem.translatef(this.x, this.y,0.0f);
        RenderSystem.colorMask(false, false, false, false);

        fill(matrixStack, this.width, this.height, 0, 0, -0xffffff);
        RenderSystem.colorMask(true, true, true, true);

        RenderSystem.translatef(-this.x, -this.y,0.0f);
        RenderSystem.depthFunc(515);

        this.renderItems(matrixStack, mouseX, mouseY, partialTicks);

        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();

        RenderSystem.pushMatrix();

        this.renderToolTip(matrixStack, mouseX, mouseY);
        RenderSystem.popMatrix();

    }

    private void renderItems(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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
        blit(matrixStack, this.x + this.width - this.scrollerWidth + 1, this.y + yOffset+ 1, this.canScroll?9:16,0,7,27);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.canScroll) {
            this.scrolled = MathHelper.clamp(this.scrolled + 4 * ((int) -delta), 0, this.listItems.size() * this.itemHeight - this.height);
            this.scrolledD = scrolled;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(this.canScroll && this.scrollerClicked) {
            this.scrolledD += dragY * 1.5;
            this.scrolled = ((int) this.scrolledD);
            this.scrolled = MathHelper.clamp(this.scrolled, 0 , this.listItems.size() * this.itemHeight - this.height);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolledD = this.scrolled;
        if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height) {
            if (mouseX > this.x + this.width - this.scrollerWidth) {
                this.scrollerClicked = true;
            } else {
                for (int i = 0; i < this.listItems.size(); i++) {

                    int y = i*itemHeight - scrolled;

                    if (y < -itemHeight) {
                        continue;
                    }


                    ListItem<T> item = this.listItems.get(i);
                    if (mouseX > this.x && mouseX < this.x + this.width - this.scrollerWidth && mouseY > this.y + y && mouseY < this.y + y + this.itemHeight) {
                        if(item.onClick(mouseX, mouseY)){
                            return true;
                        }
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (this.scrollerClicked) return;
        if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height) {

            int itemHeight = this.itemHeight - 1; // only 1 pixel between items
            for (int i = 0; i < this.listItems.size(); i++) {

                int y = i * itemHeight - scrolled;

                if (y < -itemHeight) {
                    continue;
                }

                ListItem<T> item = this.listItems.get(i);
                item.preRenderToolTip(matrixStack, this.x, this.y + y, this.width - scrollerWidth, this.height, this.itemHeight, y, mouseX, mouseY, this.getBlitOffset());

            }
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
            int ySize = MathHelper.clamp(listHeight - yOffset, 0, itemHeight);
            int yTopBorder = 3;
            int yBottomBorder = 3;
            int v = 66;
            int textureHeight = 20;
            if (ySize <= 0) return;
            if (ySize < itemHeight) {
                if (ySize < 3) {
                    yTopBorder = ySize;
                }
                yBottomBorder = 0;
                textureHeight -= 3;
            }
            if (yOffset < 0) {
                ySize += yOffset;
                yTopBorder = Math.max(0, yTopBorder + yOffset);
                v += 3 - yTopBorder;
                textureHeight -= 3 - yTopBorder;
                if (-yOffset >= itemHeight - 2) {
                    yBottomBorder = 1;
                }
                yOffset = 0;
            }
            renderBox(matrixStack, WIDGETS, x, y + yOffset, 0, v, listWidth + 1, ySize, 200, textureHeight, yTopBorder, yBottomBorder, 3, 3, zLevel);
        }

        public void renderBox(MatrixStack matrixStack, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float partialTicks) {
            GuiUtils.drawContinuousTexturedBox(matrixStack, texture, x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, partialTicks);
        }

        public void preRenderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            int ySize = MathHelper.clamp(listHeight - yOffset, 0, itemHeight);

            if (mouseX > x && mouseX < x + listWidth && mouseY > y && mouseY < y + ySize) {
                this.renderToolTip(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, zLevel);
            }
        }

        public void renderToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) { }

        public boolean onClick(double mouseX, double mouseY) {
            return false;
        }
    }
}
