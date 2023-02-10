package dev.adamraichu.servercommands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import net.minecraft.text.*;
import net.minecraft.command.argument.NumberRangeArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;

public class ServerCommands implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("servercommands");

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    LOGGER.info("Server Commands mod is present.");

    CommandRegistrationCallback.EVENT
        .register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("foo")
            .executes(context -> {
              // For versions below 1.19, replace "Text.literal" with "new LiteralText".
              context.getSource().sendMessage(Text.literal("Called /foo with no arguments"));

              return 1;
            })));

    CommandRegistrationCallback.EVENT
        .register((dispatcher, registryAccess, environment) -> dispatcher
            .register(literal("feeeze")
                .requires(source -> source.hasPermissionLevel(3))
                .then(argument("player", StringArgumentType.word()))
                .then(argument("duration", NumberRangeArgumentType.intRange()))
                .executes(ctx -> freeze(ctx.getSource(), StringArgumentType.getString(ctx, "player"), 30))));
  }

  private static int freeze(ServerCommandSource source, String playerName, Integer duration) {
    var player = source.getServer().getPlayerManager().getPlayer(playerName);
    var slowness = new StatusEffectInstance(StatusEffect.byRawId(2), duration, 4);

    player.addStatusEffect(slowness);

    return Command.SINGLE_SUCCESS;
  }
}
