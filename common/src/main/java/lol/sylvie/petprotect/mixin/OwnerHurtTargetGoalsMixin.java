package lol.sylvie.petprotect.mixin;

import lol.sylvie.petprotect.PetProtect;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ OwnerHurtByTargetGoal.class, OwnerHurtTargetGoal.class })
public class OwnerHurtTargetGoalsMixin {
    @Shadow(remap = false)
    @Final
    private TamableAnimal tameAnimal;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void petprotect$petAttackCheck(CallbackInfoReturnable<Boolean> cir) {
        if (PetProtect.shouldPreventAttack(tameAnimal.getOwner())) cir.setReturnValue(false);
    }
}
