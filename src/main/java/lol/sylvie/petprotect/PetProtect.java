package lol.sylvie.petprotect;

import lol.sylvie.petprotect.config.ConfigInstance;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PetProtect implements ModInitializer {
    public static String MOD_ID = "petprotect";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ConfigInstance config;

    @Override
    public void onInitialize() {
        File configFile = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json").toFile();
        config = ConfigInstance.fromFile(configFile);

        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (player.isSpectator() || (config.shouldIgnoreCreative() && player.isCreative()) || !config.preventPetDamage()) return ActionResult.PASS;
            if (entity instanceof TameableEntity tameable) {
                if (tameable.getOwnerUuid() == null) return ActionResult.PASS; // Mob is not tamed
                if (!(tameable.isOwner(player) && config.allowOwnerDamage())) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        // If the mod updates and a new config option is added, it won't show up in the config unless I do this :P
        Runtime.getRuntime().addShutdownHook(new Thread(() -> config.writeToFile(configFile)));
    }
}
