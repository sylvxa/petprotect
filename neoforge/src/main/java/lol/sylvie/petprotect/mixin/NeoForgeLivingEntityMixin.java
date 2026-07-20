package lol.sylvie.petprotect.mixin;

import lol.sylvie.petprotect.PetProtect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class NeoForgeLivingEntityMixin {
    @Shadow
    public abstract boolean isDeadOrDying();

    // Taken from Fabric API's "ALLOW_DEATH", copied to keep consistent between Fabric and NeoForge
    // https://github.com/FabricMC/fabric-api/blob/f5f9b311e1c5c8269af5b46e089f163df972c6f3/fabric-entity-events-v1/src/main/java/net/fabricmc/fabric/mixin/entity/event/LivingEntityMixin.java#L77
    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z", ordinal = 1))
    public boolean petprotect$checkDeath(LivingEntity livingEntity, ServerLevel level, DamageSource source, float amount) {
        return this.isDeadOrDying() && PetProtect.shouldAllowDeath(source, livingEntity);
    }
}
