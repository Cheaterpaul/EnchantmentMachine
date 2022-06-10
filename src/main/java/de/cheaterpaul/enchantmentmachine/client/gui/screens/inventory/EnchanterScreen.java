package de.cheaterpaul.enchantmentmachine.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.cheaterpaul.enchantmentmachine.EnchantmentMachineMod;
import de.cheaterpaul.enchantmentmachine.client.gui.components.ScrollableListButton;
import de.cheaterpaul.enchantmentmachine.core.ModConfig;
import de.cheaterpaul.enchantmentmachine.inventory.EnchanterContainerMenu;
import de.cheaterpaul.enchantmentmachine.network.message.EnchantingPacket;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import de.cheaterpaul.enchantmentmachine.util.Utils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class EnchanterScreen extends EnchantmentBaseScreen<EnchanterContainerMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/enchanter.png");

    private final Map<EnchantmentInstanceMod, Pair<EnchantmentInstanceMod, Integer>> enchantments = new HashMap<>();
    private ScrollableListButton<Pair<EnchantmentInstanceMod, Integer>> list;
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
        this.addRenderableWidget(list = new ScrollableListButton<>(this.leftPos + 8, this.topPos + 15, this.imageWidth - 50, this.imageHeight - 94 - 17, 21, EnchantmentItem::new));
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstanceMod> enchantments) {
        this.enchantments.clear();
        enchantments.forEach((instance, integer) -> this.enchantments.put(instance, Pair.of(instance, integer)));
        refreshActiveEnchantments();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (!this.isQuickCrafting) {
            this.list.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return true;
    }

    public void refreshActiveEnchantments() {
        ItemStack stack = this.menu.getSlot(0).getItem();
        this.itemEnchantments = EnchantmentHelper.getEnchantments(stack);
        if (stack.isEmpty()) {
            this.list.setItems(this.enchantments.values());
        } else {
            this.list.setItems(this.enchantments.values().stream().filter(pair -> stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK || pair.getKey().getEnchantment().canEnchant(stack)).collect(Collectors.toList()));
        }
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

    private class EnchantmentItem extends ScrollableListButton.ListItem<Pair<EnchantmentInstanceMod, Integer>> {

        private final ItemStack bookStack;
        private final Component name;
        private final Button button;
        private final int requiredLevels;

        public EnchantmentItem(Pair<EnchantmentInstanceMod, Integer> item) {
            super(item);
            this.bookStack = new ItemStack(Items.ENCHANTED_BOOK, item.getRight());
            EnchantmentHelper.setEnchantments(Collections.singletonMap(item.getKey().getEnchantment(), item.getKey().getLevel()), bookStack);
            this.name = item.getKey().getEnchantment().getFullname(item.getKey().getLevel());
            Style style = this.name.getStyle();
            //noinspection ConstantConditions
            if(style.getColor() != null && style.getColor().getValue() == ChatFormatting.GRAY.getColor()){
                style.withColor(ChatFormatting.WHITE);
            }
            this.button = new ImageButton(0, 0, 11, 17, 1, 208, 18, new ResourceLocation("textures/gui/recipe_book.png"), 256, 256, (button) -> EnchanterScreen.this.apply(item.getKey()), new Button.OnTooltip() {
                @Override
                public void onTooltip(@Nonnull Button button, @Nonnull PoseStack matrixStack, int mouseX, int mouseY) {
                    if (mouseX > button.x && mouseX < button.x + button.getWidth() && mouseY > button.y && mouseY < button.y + button.getHeight()) {
                        MutableComponent text;
                        if (isCompatible()) {
                            if (hasSufficientLevels()) {
                                text = new TranslatableComponent("text.enchantmentmachine.enchant_for_level", EnchantmentItem.this.requiredLevels).withStyle(ChatFormatting.GREEN);
                            } else {
                                text = new TranslatableComponent("text.enchantmentmachine.require_level", EnchantmentItem.this.requiredLevels).withStyle(ChatFormatting.YELLOW);
                            }
                        } else {
                            text = new TranslatableComponent("text.enchantmentmachine.unavailable").withStyle(ChatFormatting.RED);
                        }
                        EnchanterScreen.this.renderTooltip(matrixStack, text, mouseX, mouseY);
                    }
                }
            }, TextComponent.EMPTY);
            requiredLevels = calculateRequiredLevels();
        }

        @Override
        public boolean onClick(double mouseX, double mouseY) {
            if (!this.button.visible) return false;
            if (mouseX > this.button.x && mouseX < this.button.x + this.button.getWidth() && mouseY > this.button.y && mouseY < this.button.y + this.button.getHeight()) {
                if (isCompatible() && hasSufficientLevels()) {
                    this.button.onClick(mouseX, mouseY);
                }
                return true;
            }
            return false;
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
        public void render(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float partialTicks, float zLevel) {
            super.render(matrixStack, x, y, listWidth, listHeight, itemHeight, yOffset, mouseX, mouseY, partialTicks, zLevel);

            EnchanterScreen.this.itemRenderer.renderAndDecorateFakeItem(bookStack, x + 5, y + 2 + yOffset);
            //noinspection ConstantConditions
            EnchanterScreen.this.font.drawShadow(matrixStack, name.getString(), x + 25, y + yOffset + 5, name.getStyle().getColor().getValue());

            String count = String.valueOf(bookStack.getCount());
            EnchanterScreen.this.font.drawShadow(matrixStack, count, x + listWidth - 20, y + yOffset + 5, 0xffffff);

            this.button.x = x + listWidth - 12;
            this.button.y = y + yOffset + 2;

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
            matrixStack.pushPose();
            this.button.render(matrixStack, mouseX, mouseY, partialTicks);
            matrixStack.popPose();
            RenderSystem.setShaderColor(1, 1, 1, 1);

        }

        @Override
        public void renderToolTip(PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int yOffset, int mouseX, int mouseY, float zLevel) {
            if (mouseX > x && mouseX < x + listWidth - 12 && mouseY > y && mouseY < y + itemHeight) {
                EnchanterScreen.this.renderTooltip(matrixStack, bookStack, mouseX, mouseY);
            }
            if (button.visible) {
                this.button.renderToolTip(matrixStack, mouseX, mouseY);
            }
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
