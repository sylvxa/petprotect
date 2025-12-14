package lol.sylvie.petprotect;

import lol.sylvie.petprotect.config.ConfigInstance;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DeathProtection;
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

        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            if (player.isSpectator() || (config.shouldIgnoreCreative() && player.isCreative()) || !config.preventPetDamage()) return InteractionResult.PASS;
            if (entity instanceof TamableAnimal tameable) {
                if (tameable.getOwnerReference() == null) return InteractionResult.PASS; // Mob is not tamed
                if (!(tameable.isOwnedBy(player) && config.allowOwnerDamage())) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (!config.preventPetDamage() || !config.preventPetDeath()) return true;
            if (damageSource.isCreativePlayer() && config.shouldIgnoreCreative()) return true;
            if (entity instanceof TamableAnimal tameable) {
                if (tameable.getOwnerReference() == null) return true; // Mob is not tamed
                if (!(damageSource.getEntity() instanceof Player player && tameable.isOwnedBy(player) && config.allowOwnerDamage())) {
                    // we're not allowing this death: reset health to full
                    entity.setHealth(entity.getMaxHealth());
                    if (config.applyTotemEffects()) {
                        DeathProtection.TOTEM_OF_UNDYING.applyEffects(new ItemStack(Items.TOTEM_OF_UNDYING), entity);
                        entity.level().broadcastEntityEvent(entity, EntityEvent.PROTECTED_FROM_DEATH);
                    }
                    return false;
                }
            }
            return true;
        });
    }
}
