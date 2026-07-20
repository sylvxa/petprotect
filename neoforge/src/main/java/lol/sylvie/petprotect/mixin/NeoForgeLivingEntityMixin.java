package lol.sylvie.petprotect.mixin;

import lol.sylvie.petprotect.PetProtect;
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
    // https://github.com/FabricMC/fabric-api/blob/80e63a7be61dff7658861e1c7df8643c3e676c5b/fabric-entity-events-v1/src/main/java/net/fabricmc/fabric/mixin/entity/event/LivingEntityMixin.java#L77
    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z", ordinal = 1))
    public boolean petprotect$checkDeath(LivingEntity livingEntity, DamageSource source, float amount) {
        return this.isDeadOrDying() && PetProtect.shouldAllowDeath(source, livingEntity);
    }
}
