package io.github.adamraichu.servercommands.commands;

import static io.github.adamraichu.servercommands.ServerCommandsForge.CONFIG;
import static io.github.adamraichu.servercommands.ServerCommandsForge.LOGGER;
import static io.github.adamraichu.servercommands.ServerCommandsForge.toNumber;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.text.ParseException;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FreezeCommand {
  @SubscribeEvent
  public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
    try {
      Integer requiredPermissionLevel = toNumber(CONFIG.getProperty("cmds.freeze.permissionLevel", "2")).intValue();
      event.getDispatcher().register(literal("freeze")
          .requires((source) -> source
              .hasPermission(requiredPermissionLevel))
          .then(argument("player", EntityArgument.player()).then(argument("duration", IntegerArgumentType.integer(1))
              .executes(ctx -> {
                return freezePlayer(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"),
                    ctx.getArgument("duration", Integer.class) * 20);
              }))));
      LOGGER.info("Command /freeze registered.");
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private static int freezePlayer(CommandSourceStack source, ServerPlayer player, Integer duration) {
    MobEffectInstance slowness = new MobEffectInstance(MobEffect.byId(2), duration, 6);
    MobEffectInstance mining_fatigue = new MobEffectInstance(MobEffect.byId(4), duration, 9);
    MobEffectInstance jump_boost = new MobEffectInstance(MobEffect.byId(8), duration, -5);
    MobEffectInstance blindness = new MobEffectInstance(MobEffect.byId(15), duration, 0);
    MobEffectInstance weakness = new MobEffectInstance(MobEffect.byId(18), duration, 19);

    player.addEffect(slowness);
    player.addEffect(mining_fatigue);
    player.addEffect(jump_boost);
    player.addEffect(blindness);
    player.addEffect(weakness);

    player.sendSystemMessage(Component.literal(source.getTextName() + " froze you."));

    if (Boolean.parseBoolean(CONFIG.getProperty("cmds.freeze.logUsage", "true"))) {
      LOGGER.info(source.getTextName() + " froze " + player.getName().getString() + " for " + duration.toString()
          + " seconds.");
    }

    return Command.SINGLE_SUCCESS;
  }
}
