package de.cheaterpaul.enchantmentmachine.core;


import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ModConfig {

    /**
     * Synced to clients.
     * Only loaded on world load
     */
    public static final Server SERVER;

    private static final ForgeConfigSpec serverSpec;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static void init() {
        //This initiates the static initializers
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.SERVER, serverSpec);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SERVER::onConfigLoad);
    }

    /**
     * This is stored server side on a per world base.
     * Config is synced to clients on connect
     */
    public static class Server {

        public final ForgeConfigSpec.BooleanValue allowDisenchantingItems;
        public final ForgeConfigSpec.BooleanValue allowMixtureEnchantments;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> maxEnchantmentLevels;
        public final ForgeConfigSpec.BooleanValue allowDisenchantingCurses;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> disallowedDisenchantingEnchantments;

        private Set<Enchantment> disallowedDisenchantingEnchantmentsMap;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");
            allowDisenchantingItems = builder.comment("Whether items can be disenchanted. More vanilla like would be false").define("allowDisenchantingItems", true);
            allowMixtureEnchantments = builder.comment("Whether incompatible enchantments can be allied together").define("allowMixtureEnchantments", false);
            maxEnchantmentLevels = builder.comment("Define the max level for applying enchantments. Format is [\"enchantment_id|max_level\",\"enchantment_id|max_level\"]").defineList("maxEnchantmentLevels", Collections.emptyList(), string -> {
                if (string instanceof String) {
                    try {
                        String[] value = ((String) string).split("\\|");
                        new ResourceLocation(value[0]);
                        Integer.parseInt(value[1]);
                        return true;
                    } catch (ResourceLocationException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            });
            allowDisenchantingCurses = builder.comment("Whether curses can be removed from items", "Only relevant when `allowDisenchantingItems` is enabled").define("allowDisenchantingCurses", false);
            disallowedDisenchantingEnchantments = builder.comment("List of enchantments that can not be removed from items", "Only relevant when `allowDisenchantingItems` is enabled", "This overrides `allowDisenchantingCurses`").defineList("disallowedDisenchantingEnchantments", Collections.emptyList(), this::isResourceLocation);
            builder.pop();
        }

        private boolean isResourceLocation(Object obj) {
            if (obj instanceof String string) {
                return ResourceLocation.tryParse(string) != null;
            } else {
                return false;
            }
        }

        public Set<Enchantment> getDisallowedDisenchantingEnchantments() {
            return Objects.requireNonNullElse(this.disallowedDisenchantingEnchantmentsMap, Collections.emptySet());
        }

        public void onConfigLoad(ModConfigEvent event) {
            if (serverSpec.isLoaded()) {
                this.disallowedDisenchantingEnchantmentsMap = this.disallowedDisenchantingEnchantments.get().stream().map(ResourceLocation::new).map(ForgeRegistries.ENCHANTMENTS::getValue).collect(Collectors.toSet());
            }
        }
    }
}
