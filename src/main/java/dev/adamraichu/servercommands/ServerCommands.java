package dev.adamraichu.servercommands;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.adamraichu.servercommands.commands.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ServerCommands implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("servercommands");

  public static Properties CONFIG = new ServerCommandsConfig(FabricLoader.getInstance().getConfigDir().resolve(
      "servercommands.properties").toFile())
      .getConfig();

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    LOGGER.info("Server Side Commands mod is present.");

    FreezeCommand.register();
    TrackCommand.register();

    LOGGER.info("All commands have been registered for Server Side Commands");
  }

  public static Number toNumber(String string) throws ParseException {
    return NumberFormat.getInstance().parse(string);
  }
}
