package de.cheaterpaul.enchantmentmachine.core;


import com.electronwill.nightconfig.core.ConfigSpec;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

public class ModConfig {

    /**
     * Synced to clients.
     * Only loaded on world load
     */
    public static final Server SERVER;

    private static final ModConfigSpec serverSpec;

    static {
        final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static void init(IEventBus modbus, ModContainer container) {
        //This initiates the static initializers
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, serverSpec);
        modbus.addListener(SERVER::onConfigLoad);
    }

    /**
     * This is stored server side on a per world base.
     * Config is synced to clients on connect
     */
    public static class Server {

        public final ModConfigSpec.BooleanValue allowDisenchantingItems;
        public final ModConfigSpec.BooleanValue allowMixtureEnchantments;
        public final ModConfigSpec.ConfigValue<List<? extends String>> maxEnchantmentLevels;
        public final ModConfigSpec.BooleanValue allowDisenchantingCurses;
        public final ModConfigSpec.ConfigValue<List<? extends String>> disallowedDisenchantingEnchantments;
        public final ModConfigSpec.ConfigValue<Double> priceModifier;

        private Set<ResourceLocation> disallowedDisenchantingEnchantmentsMap;
        private Map<ResourceLocation, Integer> maxEnchantmentLevelsMap;

        Server(ModConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");
            allowDisenchantingItems = builder.comment("Whether items can be disenchanted. More vanilla like would be false").define("allowDisenchantingItems", true);
            allowMixtureEnchantments = builder.comment("Whether incompatible enchantments can be allied together").define("allowMixtureEnchantments", false);
            maxEnchantmentLevels = builder.comment("Define the max level for applying enchantments. Format is [\"enchantment_id|max_level\",\"enchantment_id|max_level\"]").defineList("maxEnchantmentLevels", Collections.emptyList(), string -> {
                if (string instanceof String) {
                    try {
                        String[] value = ((String) string).split("\\|");
                        ResourceLocation.parse(value[0]);
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
            priceModifier = builder.comment("Multiplies the final price of applying an enchantment", "Set to 0 to disable cost").define("priceModifier", 1.0);
            builder.pop();
        }

        private boolean isResourceLocation(Object obj) {
            if (obj instanceof String string) {
                return ResourceLocation.tryParse(string) != null;
            } else {
                return false;
            }
        }

        public Set<ResourceLocation> getDisallowedDisenchantingEnchantments() {
            return Objects.requireNonNullElse(this.disallowedDisenchantingEnchantmentsMap, Collections.emptySet());
        }

        @Unmodifiable
        public Map<ResourceLocation, Integer> getMaxEnchantmentLevels() {
            return Objects.requireNonNullElse(this.maxEnchantmentLevelsMap, Collections.emptyMap());
        }

        public void onConfigLoad(ModConfigEvent event) {
            if (serverSpec.isLoaded()) {
                this.disallowedDisenchantingEnchantmentsMap = this.disallowedDisenchantingEnchantments.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet());
                this.maxEnchantmentLevelsMap = this.maxEnchantmentLevels.get().stream().map(s -> {
                    String[] maxLevels = s.split("\\|");
                    return Pair.of(ResourceLocation.parse(maxLevels[0]), Integer.parseInt(maxLevels[1]));
                }).collect(Collectors.toUnmodifiableMap(Pair::getLeft, Pair::getRight));
            }
        }
    }
}
