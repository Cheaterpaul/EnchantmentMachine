package de.cheaterpaul.enchantmentmachine.core;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

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
    }

    /**
     * This is stored server side on an per world base.
     * Config is synced to clients on connect
     */
    public static class Server {

        public final ForgeConfigSpec.BooleanValue allowDisenchantingItems;
        public final ForgeConfigSpec.BooleanValue allowMixtureEnchantments;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");
            allowDisenchantingItems = builder.comment("Whether items can be disenchanted. More vanilla like would be false").define("allowDisenchantingItems", true);
            allowMixtureEnchantments = builder.comment("Whether incompatible enchantments can be allied together").define("allowMixtureEnchantments", false);
            builder.pop();
        }
    }
}
