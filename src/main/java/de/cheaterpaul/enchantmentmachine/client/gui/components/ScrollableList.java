package de.cheaterpaul.enchantmentmachine.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ScreenUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * make sure that {@link net.minecraft.client.gui.components.events.ContainerEventHandler#mouseDragged(double, double, int, double, double)} is being called
 */
public class ScrollableList<T> extends AbstractWidget {

    private static final ResourceLocation MISC = new ResourceLocation(REFERENCE.MODID, "textures/gui/misc.png");

    private final ItemCreator<T> itemSupplier;
    private final int itemHeight;
    private final int scrollerWidth = 9;

    private final List<ListItem<T>> listItems = new ArrayList<>();
    private int scrolled;
    private double scrolledD;
    private boolean scrollerClicked;
    private boolean canScroll = true;


    public ScrollableList(int xPos, int yPos, int width, int height, int itemHeight) {
        this(xPos, yPos, width, height, itemHeight, ListItem::new);
    }

    public ScrollableList(int xPos, int yPos, int width, int height, int itemHeight, ItemCreator<T> itemSupplier) {
        super(xPos, yPos, width, height, Component.empty());
        this.itemHeight = itemHeight;
        this.itemSupplier = itemSupplier;
    }

    public void setItems(Collection<T> elements) {
        this.listItems.clear();
        elements.forEach(item -> this.listItems.add(this.itemSupplier.apply(this.width - this.scrollerWidth, this.itemHeight, item)));
        this.setScrolled(Mth.clamp(this.scrolled, 0, Math.max(0, this.listItems.size() * this.itemHeight - this.height)));
        this.canScroll = this.listItems.size() * this.itemHeight > this.height;
    }

    public void addItem(T element) {
        this.listItems.add(this.itemSupplier.apply(this.width - this.scrollerWidth, this.itemHeight, element));
    }

    public void removeItem(T element) {
        this.listItems.removeIf(item -> item.item == element);
        if (this.scrolled > this.listItems.size() * this.itemHeight - this.height) {
            this.setScrolled(this.listItems.size() * this.itemHeight - this.height);
        }
    }

    public void setScrolled(int scrolled) {
        this.scrolledD = this.scrolled = scrolled;
    }

    public int getScrolled() {
        return scrolled;
    }

    @Override
    public void renderButton(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();
        RenderSystem.enableDepthTest();
        matrixStack.translate(0, 0, 950);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        matrixStack.translate(0.0F, 0.0F, -950.0F);

        RenderSystem.depthFunc(518);
        matrixStack.translate(this.getX(), this.getY(), 0);
        fill(matrixStack, this.width, this.height, 0, 0, -0xff0000);
        matrixStack.translate(-this.getX(), -this.getY(), 0);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();

        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);

        this.renderItems(matrixStack, mouseX, mouseY, partialTicks);

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(518);
        matrixStack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        matrixStack.translate(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        matrixStack.popPose();
    }

    private void renderBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        ScreenUtils.blitWithBorder(matrixStack, new ResourceLocation("textures/gui/widgets.png"), getX(), getY(), 0, 46, this.width - this.scrollerWidth + 1, this.height, 200, 20, 3, 3, 3, 3, this.getBlitOffset());
    }

    private void renderItems(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.pushPose();
        int itemHeight = this.itemHeight - 1; // only 1 pixel between items
        for (int i = 0; i < this.listItems.size(); i++) {

            int y = i * itemHeight - scrolled;

            matrixStack.pushPose();
            matrixStack.translate(this.getX(), this.getY() + y, 0);
            ListItem<T> item = this.listItems.get(i);
            item.render(matrixStack, mouseX - getX(), mouseY - getY() - y, partialTicks);
            matrixStack.popPose();

        }
        this.renderScrollBar(matrixStack, mouseX, mouseY, partialTicks);
        matrixStack.popPose();
    }

    private void renderScrollBar(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        ScreenUtils.blitWithBorder(matrixStack, MISC, this.getX() + this.width - this.scrollerWidth, this.getY(), 0, 0, 9, this.height, 9, 200, 2, getBlitOffset());
        this.renderScroller(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderScroller(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int scrollerHeight = 27;
        int scrollHeight = this.height - 2 - scrollerHeight;
        float perc = (float) this.scrolled / (float) (this.listItems.size() * this.itemHeight - this.height);
        int yOffset = (int) (scrollHeight * perc);
        RenderSystem.setShaderTexture(0, MISC);
        blit(matrixStack, this.getX() + this.width - this.scrollerWidth + 1, this.getY() + yOffset + 1, this.canScroll ? 9 : 16, 0, 7, 27);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.canScroll) {
            this.scrolled = Mth.clamp(this.scrolled + 4 * ((int) -delta), 0, this.listItems.size() * this.itemHeight - this.height);
            this.scrolledD = scrolled;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.canScroll && this.scrollerClicked) {
            double perc = (dragY / (this.height - 27));
            double s = (this.listItems.size() * this.itemHeight - this.height) * perc;
            this.scrolledD += s;
            this.scrolled = ((int) scrolledD);
            this.scrolled = Mth.clamp(this.scrolled, 0, this.listItems.size() * this.itemHeight - this.height);
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        this.scrolledD = this.scrolled;
        return super.clicked(pMouseX, pMouseY);
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        if (pMouseX > this.getX() + this.width - this.scrollerWidth) {
            this.scrollerClicked = true;
        } else {
            for (int i = 0; i < this.listItems.size(); i++) {
                ListItem<T> item = this.listItems.get(i);
                item.mouseClicked(pMouseX - this.getX(), pMouseY - this.getY() - this.scrolled, 0);
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
        ListItem<T> apply(int width, int height, T item);
    }

    public static class ListItem<T> extends AbstractWidget implements ContainerEventHandler {

        protected final T item;

        public ListItem(int width, int height, T item) {
            super(0,0, width, height, Component.empty());
            this.item = item;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narration) {

        }

        @Override
        public List<? extends GuiEventListener> children() {
            return null;
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            ContainerEventHandler.super.mouseClicked(pMouseX, pMouseY, 0);
        }

        @Override
        public void onRelease(double pMouseX, double pMouseY) {
            ContainerEventHandler.super.mouseReleased(pMouseX, pMouseY, 0);
        }

        @Override
        protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
            ContainerEventHandler.super.mouseDragged(pMouseX, pMouseY, 0, pDragX, pDragY);
        }

        @Override
        public boolean isDragging() {
            return false;
        }

        @Override
        public void setDragging(boolean pIsDragging) {

        }

        @Nullable
        @Override
        public GuiEventListener getFocused() {
            return this.focused;
        }

        private @Nullable GuiEventListener focused;

        @Override
        public void setFocused(@Nullable GuiEventListener pFocused) {
            this.focused = pFocused;
        }
    }
}
