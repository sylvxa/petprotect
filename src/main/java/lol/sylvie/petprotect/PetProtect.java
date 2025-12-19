package lol.sylvie.petprotect;

import lol.sylvie.petprotect.config.ConfigInstance;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class PetProtect implements ModInitializer {
    public static String MOD_ID = "petprotect";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ConfigInstance config;

    private static boolean shouldAllow(Entity target, @Nullable Entity attacker) {
        if (!config.preventPetDamage() || !(target instanceof TamableAnimal tameable)) return true;
        if (!(attacker instanceof ServerPlayer) && config.onlyPreventPlayers()) return true;

        if (tameable.getOwnerReference() == null || (attacker instanceof ServerPlayer player &&
                ((tameable.isOwnedBy(player) && config.allowOwnerDamage()) || // Owner check
                (config.shouldIgnoreCreative() && player.isCreative()))))     // Creative check
            return true;

        return false;
    }

    @Override
    public void onInitialize() {
        File configFile = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json").toFile();
        config = ConfigInstance.fromFile(configFile);
        config.writeToFile(configFile);

        AttackEntityCallback.EVENT.register((player, level, hand, entity, result) ->
                shouldAllow(entity, player) ? InteractionResult.PASS : InteractionResult.FAIL);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> shouldAllow(entity, source.getEntity()));

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
