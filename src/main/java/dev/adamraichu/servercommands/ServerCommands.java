package dev.adamraichu.servercommands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ServerCommands implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("servercommands");

  private Properties CONFIG;

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    CONFIG = new ServerCommandsConfig(FabricLoader.getInstance().getConfigDir().resolve(
        "servercommands.properties").toFile())
        .getConfig();

    LOGGER.info("Server Side Commands mod is present.");

    try {

      Integer requiredPermissionLevel = toNumber(CONFIG.getProperty("cmds.freeze.permissionLevel", "2")).intValue();

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
      LOGGER.info("Commands registered");
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

    LOGGER.info(source.getName() + " froze " + player.getName().getString() + ".");

    return Command.SINGLE_SUCCESS;
  }

  private Number toNumber(String string) throws ParseException {
    return NumberFormat.getInstance().parse(string);
  }
}
