package lol.sylvie.petprotect.mixin;

import lol.sylvie.petprotect.PetProtect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OwnerHurtByTargetGoal.class)
public class TrackOwnerAttackerGoalMixin {
    @Shadow @Final private TamableAnimal tameAnimal;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void canStart(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity owner = tameAnimal.getOwner();
        if (owner == null || owner.getLastHurtByMob() == null || !owner.getLastHurtByMob().isAlwaysTicking()) return;
        if (PetProtect.config.preventPetAttack()) cir.setReturnValue(false);
    }
}
