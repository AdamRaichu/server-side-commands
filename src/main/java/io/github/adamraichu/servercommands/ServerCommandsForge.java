package io.github.adamraichu.servercommands;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Properties;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io.github.adamraichu.servercommands.commands.FreezeCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ServerCommandsForge.MODID)
public class ServerCommandsForge {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "servercommands";
  // Directly reference a slf4j logger
  public static final Logger LOGGER = LogUtils.getLogger();

  public static Properties CONFIG = new ServerCommandsConfig(
      new File(".").toPath().resolve("config").resolve(MODID + ".properties").toFile()).getConfig();

  public ServerCommandsForge() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    // Register freeze command
    MinecraftForge.EVENT_BUS.register(FreezeCommand.class);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // Some common setup code
    LOGGER.info("Server Side Commands (Forge) mod is present.");
    LOGGER.info("CONFIG path: " + new File(".").toPath().resolve("config").resolve(MODID + ".properties").toFile()
        .getPath());
  }

  public static Number toNumber(String string) throws ParseException {
    return NumberFormat.getInstance().parse(string);
  }
}
