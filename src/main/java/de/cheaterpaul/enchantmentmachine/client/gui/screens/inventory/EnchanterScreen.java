package de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.client.gui.components.ContainerList;
import de.cheaterpaul.enchantmentmachine.client.gui.components.EnchantmentItem;
import de.cheaterpaul.enchantmentmachine.client.gui.components.SimpleList;
import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.MultilineTooltip;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ScreenUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class EnchanterScreen extends EnchantmentBaseScreen<EnchanterContainerMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchanter.png");

    private final Map<EnchantmentInstanceMod, Pair<EnchantmentInstanceMod, Integer>> enchantments = new HashMap<>();
    private ContainerList<EnchantmentItem> list;
    private Map<Enchantment, Integer> itemEnchantments = new HashMap<>();


    public EnchanterScreen(EnchanterContainerMenu container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
        this.imageWidth = 232;
        this.imageHeight = 241;
        this.inventoryLabelX = 36;
        this.inventoryLabelY = this.imageHeight - 94;
        container.setListener(this::refreshActiveEnchantments);
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        refreshActiveEnchantments();
    }

    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(this.list = ContainerList.<EnchantmentItem>builder(this.leftPos + 8, this.topPos + 15, this.imageWidth - 70, this.imageHeight - 94 - 17).build());
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if(!(this.getFocused() != null && this.isDragging() && pButton == 0 && this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY))) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        return true;
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstanceMod> enchantments) {
        this.enchantments.clear();
        enchantments.forEach((instance, integer) -> this.enchantments.put(instance, Pair.of(instance, integer)));
        refreshActiveEnchantments();
    }

    public void refreshActiveEnchantments() {
        ItemStack stack = this.menu.getSlot(0).getItem();
        this.itemEnchantments = EnchantmentHelper.getEnchantments(stack);

        List<Pair<EnchantmentInstanceMod, Integer>> availableEnchantments;
        if (stack.isEmpty()) {
            availableEnchantments = this.enchantments.values().stream().sorted(Comparator.comparing(o -> o.getKey().getEnchantmentName().getString())).collect(Collectors.toList());
        } else {
            availableEnchantments = this.enchantments.values().stream().filter(pair -> stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK || pair.getKey().getEnchantment().canEnchant(stack)).sorted(Comparator.comparing(o -> o.getKey().getEnchantmentName().getString())).collect(Collectors.toList());
        }
        this.list.replace(availableEnchantments.stream().map(entry -> new EnchantmentItem(Pair.of(entry.getKey(), entry.getValue()))).collect(Collectors.toList()));
    }

    private void apply(EnchantmentInstanceMod instance) {
        if (this.menu.getSlot(0).hasItem()) {
            if (ModConfig.SERVER.allowMixtureEnchantments.get() || EnchantmentHelper.isEnchantmentCompatible(itemEnchantments.keySet(), instance.getEnchantment()) || hasEqualEnchantments(itemEnchantments, instance)) {
                EnchantmentMachineMod.DISPATCHER.sendToServer(new EnchantingPacket(Collections.singletonList(instance)));
                Pair<EnchantmentInstanceMod, Integer> value = this.enchantments.get(instance);
                if (value.getValue() > 1) {
                    this.enchantments.put(instance, Pair.of(instance, value.getValue() - 1));
                } else {
                    this.enchantments.remove(instance);
                }
            }
        }
        refreshActiveEnchantments();
    }

    private boolean hasEqualEnchantments(Map<Enchantment, Integer> itemEnchantments, EnchantmentInstanceMod enchantment) {
        for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            if (entry.getKey() == enchantment.getEnchantment()) {
                if (entry.getKey().getMaxLevel() != entry.getValue() && entry.getValue() <= enchantment.getLevel()) {
                    return true;
                }
            }
        }
        return false;
    }

    private class EnchantmentItem extends ContainerList.Entry<EnchantmentItem> {

        private final ItemStack bookStack;
        private final Component name;
        private final Button button;
        private final AbstractWidget text;
        private final int requiredLevels;
        private final Pair<EnchantmentInstanceMod, Integer> item;

        public EnchantmentItem(Pair<EnchantmentInstanceMod, Integer> item) {
            this.bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
            this.item = item;
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(), item.getKey().getLevel()), bookStack);
            this.name = item.getKey().getEnchantmentName();
            Style style = this.name.getStyle();
            //noinspection ConstantConditions
            if(style.getColor() == null || style.getColor().getValue() == ChatFormatting.GRAY.getColor()) {
                ((MutableComponent) this.name).withStyle(style.withColor(ChatFormatting.WHITE));
            }
            this.widgets.add(this.button = new ImageButton(0, 2, 11, 17, 1, 208, 18, new ResourceLocation("textures/gui/recipe_book.png"), 256, 256, (button) -> EnchanterScreen.this.apply(item.getKey()), Component.empty()));
            MutableComponent text;
            if (isCompatible()) {
                if (hasSufficientLevels()) {
                    text = Component.translatable("text.enchantmentmachine.enchant_for_level", EnchantmentItem.this.requiredLevels).withStyle(ChatFormatting.GREEN);
                } else {
                    text = Component.translatable("text.enchantmentmachine.require_level", EnchantmentItem.this.requiredLevels).withStyle(ChatFormatting.YELLOW);
                }
            } else {
                text = Component.translatable("text.enchantmentmachine.unavailable").withStyle(ChatFormatting.RED);
            }
            this.button.setTooltip(Tooltip.create(text));
            this.requiredLevels = calculateRequiredLevels();
            this.widgets.add(this.text = new MultiLineTextWidget(this.name, Minecraft.getInstance().font).m_269098_(200));
            this.text.m_264152_(25,5);

        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            GuiEventListener guieventlistener = null;

            for(GuiEventListener guieventlistener1 : List.copyOf(this.children())) {
                if (guieventlistener1.mouseClicked(pMouseX, pMouseY + EnchanterScreen.this.list.getScrollAmount(), pButton)) {
                    guieventlistener = guieventlistener1;
                }
            }

            if (guieventlistener != null) {
                this.setFocused(guieventlistener);
                if (pButton == 0) {
                    this.setDragging(true);
                }

                return true;
            } else {
                return false;
            }
        }

        private boolean isCompatible() {
            EnchantmentInstanceMod s = this.item.getKey();
            for (Map.Entry<Enchantment, Integer> entry : EnchanterScreen.this.itemEnchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                if (enchantment == s.getEnchantment()) { //Combine enchantments if it is already present. Choose highest level or level +1 if both have the same.
                    int newLevel = Math.min(enchantment.getMaxLevel(), s.getLevel() == entry.getValue() ? s.getLevel() + 1 : Math.max(s.getLevel(), entry.getValue()));
                    s = new EnchantmentInstanceMod(enchantment, newLevel); //Override enchInst in loop.
                }
            }
            return s.canEnchant() && ((ModConfig.SERVER.allowMixtureEnchantments.get() || EnchantmentHelper.isEnchantmentCompatible(EnchanterScreen.this.itemEnchantments.keySet(), this.item.getKey().getEnchantment())) || hasEqualEnchantments(EnchanterScreen.this.itemEnchantments, this.item.getKey()));
        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            this.button.visible = EnchanterScreen.this.menu.getSlot(0).hasItem();
            ScreenUtils.blitWithBorder(pPoseStack, WIDGETS_LOCATION, pLeft, pTop, 0, 46 + 21, pWidth, pHeight+5, 200, 18, 2, 3, 2, 2, 0);
            EnchanterScreen.this.itemRenderer.m_274336_(pPoseStack, bookStack, pLeft+5, pTop+1);
            EnchanterScreen.this.font.drawShadow(pPoseStack, name, pLeft + 25, pTop +5,-1);
            String count = String.valueOf(bookStack.getCount());
            EnchanterScreen.this.font.drawShadow(pPoseStack, count, pLeft + pWidth - 20, pTop +5, 0xffffff);
            this.button.m_264152_(pLeft + pWidth - 12, pTop +1);
            this.text.m_264152_(pLeft + 25,pTop + 5);

            this.button.visible = EnchanterScreen.this.menu.getSlot(0).hasItem();
            if (isCompatible()) {
                if (hasSufficientLevels()) {
                    RenderSystem.setShaderColor(0.2f, 1f, 0.4f, 1);
                } else {
                    RenderSystem.setShaderColor(0.5f, 0.4f, 0.2f, 1);
                }
            } else {
                RenderSystem.setShaderColor(1f, 0.2f, 0.4f, 1);
            }
            pPoseStack.pushPose();

            if (pIsMouseOver) {
                setTooltipForNextRenderPass(EnchanterScreen.this.getTooltipFromItem(this.bookStack).stream().flatMap(a -> Tooltip.splitTooltip(Minecraft.getInstance(),a).stream()).toList());
            }

            this.button.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            pPoseStack.popPose();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

        private int calculateRequiredLevels() {
            Pair<EnchantmentInstanceMod, Integer> result = Utils.tryApplyEnchantment(this.item.getKey(), EnchanterScreen.this.itemEnchantments, true);
            return result == null ? -1 : result.getRight();
        }

        private boolean hasSufficientLevels() {
            return EnchanterScreen.this.menu.getPlayer().experienceLevel >= this.requiredLevels || EnchanterScreen.this.menu.getPlayer().getAbilities().instabuild;
        }
    }
}
