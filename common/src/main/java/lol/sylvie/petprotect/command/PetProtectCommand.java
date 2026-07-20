package lol.sylvie.petprotect.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lol.sylvie.petprotect.Constants;
import lol.sylvie.petprotect.PetProtect;
import lol.sylvie.petprotect.config.Configuration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class PetProtectCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(Constants.MOD_ID).requires(e -> e.hasPermission(4));

        LiteralArgumentBuilder<CommandSourceStack> reload = Commands.literal("reload");

        reload.executes(context -> {
            CommandSourceStack source = context.getSource();
            Configuration newConfig = Configuration.unsafeLoad();

            if (newConfig != null) {
                PetProtect.CONFIG = newConfig;
                source.sendSuccess(() -> Component.translatable("commands.reload.success"), true);
                return 0;
            }

            source.sendFailure(Component.translatable("commands.reload.failure"));
            return -1;
        });

        root.then(reload);

        dispatcher.register(root);
    }
}
