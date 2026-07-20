package lol.sylvie.petprotect;

import lol.sylvie.petprotect.config.Configuration;
import lol.sylvie.petprotect.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static lol.sylvie.petprotect.Constants.LOG;

public class PetProtect {
    public static Configuration CONFIG;

    public static boolean shouldAllowDamage(Entity target, @Nullable Entity attacker) {
        if (!CONFIG.preventPetDamage || !(target instanceof TamableAnimal tameable)) return true;
        if (!(attacker instanceof ServerPlayer) && CONFIG.onlyPreventPlayers) return true;

        if (tameable.getOwnerUUID() == null ||
                (attacker instanceof ServerPlayer player &&                 // Player-specific checks
                ((CONFIG.allowOwnerDamage && tameable.isOwnedBy(player)) || // Owner check
                (CONFIG.ignoreCreative && player.isCreative()))))           // Creative check
            return true;

        return false;
    }

    public static boolean shouldPreventAttack(LivingEntity owner, LivingEntity target) {
        if (owner == null || // No owner
            target == null || !target.isAlwaysTicking() // Attacker is gone or isn't a player
        ) return false;
        return PetProtect.CONFIG.preventPetAttack;
    }

    public static boolean shouldAllowDeath(DamageSource source, @Nullable LivingEntity entity) {
        if (!(entity instanceof TamableAnimal tameable) || tameable.getOwnerUUID() == null) return true;

        if (!CONFIG.preventPetDamage || !CONFIG.preventPetDeath) return true; // Mod or this specific feature is disabled
        if (source.isCreativePlayer() && CONFIG.ignoreCreative) return true;  // Death is caused by a creative player
        if (source.getEntity() instanceof Player player && tameable.isOwnedBy(player) && CONFIG.allowOwnerDamage) return true; // Death is caused by the owner directly

        entity.setHealth(entity.getMaxHealth());
        if (CONFIG.applyTotemEffects) {
            // this is hardcoded in LivingEntity in 1.21.1
            // im far too lazy to access widener it
            entity.removeAllEffects();
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            entity.level().broadcastEntityEvent(entity, (byte) 35);
        }

        return false;
    }

    public static void init() {
        LOG.info("{} on {}", Constants.MOD_NAME, Services.PLATFORM.getPlatformName());

        CONFIG = Configuration.load();
    }
}