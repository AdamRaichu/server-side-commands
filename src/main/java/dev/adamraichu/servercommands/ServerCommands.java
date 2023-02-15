package dev.adamraichu.servercommands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NumberRangeArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import net.minecraft.text.*;

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
            .register(literal("freeze")
                .requires(source -> source.hasPermissionLevel(3))
                .then(argument("player", StringArgumentType.word())
                    .then(argument("duration", NumberRangeArgumentType.intRange())
                        .executes(ctx -> freeze(ctx, ctx.getArgument("player", String.class), 30))))));
  }

  private static int freeze(CommandContext<ServerCommandSource> context, String playerName, Integer duration)
      throws CommandSyntaxException {
    var player = context.getSource().getServer().getPlayerManager().getPlayer(playerName);
    var slowness = new StatusEffectInstance(StatusEffect.byRawId(2), duration, 4);

    player.addStatusEffect(slowness);

    return Command.SINGLE_SUCCESS;
  }
}
