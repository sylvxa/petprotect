package lol.sylvie.petprotect.mixin;

import lol.sylvie.petprotect.PetProtect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrackOwnerAttackerGoal.class)
public class TrackOwnerAttackerGoalMixin {
    @Shadow @Final private TameableEntity tameable;

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    public void canStart(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity owner = tameable.getOwner();
        if (owner == null || owner.getAttacker() == null || !owner.getAttacker().isPlayer()) return;
        if (PetProtect.config.preventPetAttack()) cir.setReturnValue(false);
    }
}
