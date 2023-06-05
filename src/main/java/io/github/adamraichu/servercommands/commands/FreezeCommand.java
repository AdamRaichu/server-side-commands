package io.github.adamraichu.servercommands.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.text.ParseException;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import io.github.adamraichu.servercommands.ServerCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FreezeCommand {
  public static void register() {
    try {

      Integer requiredPermissionLevel = ServerCommands
          .toNumber(ServerCommands.CONFIG.getProperty("cmds.freeze.permissionLevel", "2")).intValue();

      CommandRegistrationCallback.EVENT
          .register((dispatcher, registryAccess, environment) -> dispatcher
              .register(literal("freeze")
                  .requires((source) -> source.hasPermissionLevel(requiredPermissionLevel))
                  .then(argument("player", EntityArgumentType.player())
                      .then(argument("duration", IntegerArgumentType.integer(1))
                          .executes(ctx -> {
                            return freezePlayer(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player"),
                                ctx.getArgument("duration", Integer.class) * 20);
                          })))));
      ServerCommands.LOGGER.info("Command `/freeze` registered.");
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private static int freezePlayer(ServerCommandSource source, ServerPlayerEntity player, Integer duration) {
    StatusEffectInstance slowness = new StatusEffectInstance(StatusEffect.byRawId(2), duration, 6);
    StatusEffectInstance mining_fatigue = new StatusEffectInstance(StatusEffect.byRawId(4), duration, 9);
    StatusEffectInstance jump_boost = new StatusEffectInstance(StatusEffect.byRawId(8), duration, -5);
    StatusEffectInstance blindness = new StatusEffectInstance(StatusEffect.byRawId(15), duration, 0);
    StatusEffectInstance weakness = new StatusEffectInstance(StatusEffect.byRawId(18), duration, 19);

    player.addStatusEffect(slowness);
    player.addStatusEffect(mining_fatigue);
    player.addStatusEffect(jump_boost);
    player.addStatusEffect(blindness);
    player.addStatusEffect(weakness);

    player.sendMessage(Text.literal(source.getName() + " froze you."));

    if (Boolean.parseBoolean(ServerCommands.CONFIG.getProperty("cmds.freeze.logUsage", "true"))) {
      ServerCommands.LOGGER.info(source.getName() + " froze " + player.getName().getString() + ".");
    }

    return Command.SINGLE_SUCCESS;
  }
}
