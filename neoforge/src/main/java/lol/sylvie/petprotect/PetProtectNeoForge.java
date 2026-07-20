package lol.sylvie.petprotect;

import com.mojang.brigadier.CommandDispatcher;
import lol.sylvie.petprotect.command.PetProtectCommand;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Constants.MOD_ID)
public class PetProtectNeoForge {
    public PetProtectNeoForge(IEventBus eventBus) {
        PetProtect.init();

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        PetProtectCommand.register(dispatcher);
    }
}