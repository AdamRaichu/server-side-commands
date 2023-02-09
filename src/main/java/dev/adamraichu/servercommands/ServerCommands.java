package dev.adamraichu.servercommands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.text.*;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  }
}
