package de.cheaterpaul.enchantmentmachine.client.gui.screens;

import de.cheaterpaul.enchantmentmachine.client.gui.components.EnchantmentItem;
import de.cheaterpaul.enchantmentmachine.client.gui.components.SimpleList;
import de.cheaterpaul.enchantmentmachine.util.EnchantmentInstanceMod;
import de.cheaterpaul.enchantmentmachine.util.REFERENCE;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class StorageScreen extends Screen {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "textures/gui/container/enchantment.png");
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
        this.list = SimpleList.<EnchantmentItem>builder(this.guiLeft + 10, this.guiTop + 10, this.xSize - 25, this.ySize - 20).components(List.of()).build();
        this.addRenderableWidget(this.list);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int p_296369_, int p_296477_, float p_294317_) {
        super.renderBackground(guiGraphics, p_296369_, p_296477_, p_294317_);
        int i = this.guiLeft;
        int j = this.guiTop;
        guiGraphics.blit(BACKGROUND, i, j, 0, 0, this.xSize, this.ySize);
    }

    public void updateEnchantments(Object2IntMap<EnchantmentInstanceMod> enchantments) {
        this.enchantments = enchantments;
        this.list.replaceEntries(this.enchantments.object2IntEntrySet().stream().map(entry -> new EnchantmentItem(Pair.of(entry.getKey(), entry.getIntValue()))).toList());
    }

}
