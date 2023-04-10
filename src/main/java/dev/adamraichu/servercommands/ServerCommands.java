package dev.adamraichu.servercommands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;

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
        .register((dispatcher, registryAccess, environment) -> dispatcher
            .register(literal("freeze")
                .requires(source -> source.hasPermissionLevel(2))
                .then(argument("player", StringArgumentType.word())
                    .then(argument("duration", IntegerArgumentType.integer(1))
                        .executes(ctx -> {
                          return freeze(ctx, ctx.getArgument("player", String.class),
                              ctx.getArgument("duration", Integer.class) * 20);
                        })))));
  }

  private static int freeze(CommandContext<ServerCommandSource> context, String playerName, Integer duration)
      throws CommandSyntaxException {
    var player = context.getSource().getServer().getPlayerManager().getPlayer(playerName);

    if (Objects.isNull(player)) {
      context.getSource().sendError(Text.literal("Player does not exist"));
      return 0;
    }

    var slowness = new StatusEffectInstance(StatusEffect.byRawId(2), duration, 6);
    var mining_fatigue = new StatusEffectInstance(StatusEffect.byRawId(4), duration, 9);
    var jump_boost = new StatusEffectInstance(StatusEffect.byRawId(8), duration, -5);
    var blindness = new StatusEffectInstance(StatusEffect.byRawId(15), duration, 0);
    var weakness = new StatusEffectInstance(StatusEffect.byRawId(18), duration, 19);

    player.addStatusEffect(slowness);
    player.addStatusEffect(mining_fatigue);
    player.addStatusEffect(jump_boost);
    player.addStatusEffect(blindness);
    player.addStatusEffect(weakness);

    player.sendMessage(Text.literal(context.getSource().getName() + " froze you."));

    return Command.SINGLE_SUCCESS;
  }
}
