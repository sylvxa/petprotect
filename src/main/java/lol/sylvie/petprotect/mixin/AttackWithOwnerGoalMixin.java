package lol.sylvie.petprotect.mixin;

import lol.sylvie.petprotect.PetProtect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttackWithOwnerGoal.class)
public class AttackWithOwnerGoalMixin {

    @Shadow @Final private TameableEntity tameable;

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    public void canStart(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity owner = tameable.getOwner();
        if (owner == null || owner.getAttacking() == null || !owner.getAttacking().isPlayer()) return;
        if (PetProtect.config.preventPetAttack()) cir.setReturnValue(false);
    }
}
