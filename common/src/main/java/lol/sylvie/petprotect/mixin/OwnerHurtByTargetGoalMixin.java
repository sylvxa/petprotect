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
public class OwnerHurtByTargetGoalMixin {
    @Shadow
    @Final
    private TamableAnimal tameAnimal;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void petprotect$petAttackCheck(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity owner = tameAnimal.getOwner();
        if (PetProtect.shouldPreventAttack(owner, owner == null ? null : owner.getLastHurtByMob())) cir.setReturnValue(false);
    }
}