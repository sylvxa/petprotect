package lol.sylvie.petprotect;

import lol.sylvie.petprotect.config.ConfigInstance;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        config.writeToFile(configFile);

        // Block direct melee attacks via interaction callback
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (player.isSpectator() || (config.shouldIgnoreCreative() && player.isCreative()) || !config.preventPetDamage()) return ActionResult.PASS;
            if (entity instanceof TameableEntity tameable) {
                if (tameable.getOwnerReference() == null) return ActionResult.PASS; // Mob is not tamed
                if (!(tameable.isOwner(player) && config.allowOwnerDamage())) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        // Prevent death when configured (already present)
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (!config.preventPetDamage() || !config.preventPetDeath()) return true;
            if (damageSource.isSourceCreativePlayer() && config.shouldIgnoreCreative()) return true;
            if (entity instanceof TameableEntity tameable) {
                if (tameable.getOwnerReference() == null) return true; // Mob is not tamed
                if (!(damageSource.getAttacker() instanceof PlayerEntity player && tameable.isOwner(player) && config.allowOwnerDamage())) {
                    // we're not allowing this death: reset health to full
                    entity.setHealth(entity.getMaxHealth());
                    if (config.applyTotemEffects()) {
                        DeathProtectionComponent.TOTEM_OF_UNDYING.applyDeathEffects(new ItemStack(Items.TOTEM_OF_UNDYING), entity);
                        entity.getEntityWorld().sendEntityStatus(entity, EntityStatuses.USE_TOTEM_OF_UNDYING);
                    }
                    return false;
                }
            }
            return true;
        });

        // Block indirect player damage (projectiles, splash, etc.) separately
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!config.preventIndirectPetDamage()) return true;
            if (!(entity instanceof TameableEntity tameable)) return true;
            if (tameable.getOwnerReference() == null) return true;

            Entity attacker = source.getAttacker();
            Entity origin = source.getSource();
            PlayerEntity player = attacker instanceof PlayerEntity p ? p :
                                  origin instanceof PlayerEntity p2 ? p2 : null;
            if (player == null) return true;
            if (player.isCreative() && config.shouldIgnoreCreative()) return true;
            if (tameable.isOwner(player) && config.allowOwnerDamage()) return true;

            return false; // cancel indirect damage
        });
    }
}
