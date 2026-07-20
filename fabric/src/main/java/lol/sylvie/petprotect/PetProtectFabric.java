package lol.sylvie.petprotect;

import lol.sylvie.petprotect.command.PetProtectCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public class PetProtectFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PetProtect.init();

        // Command registration is generally platform specific
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            PetProtectCommand.register(dispatcher);
        });

        // Fabric API has this event, implemented via mixin on NeoForge
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> PetProtect.shouldAllowDeath(damageSource, entity));
    }
}
