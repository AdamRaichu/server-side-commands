package dev.adamraichu.servercommands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ServerCommandsConfig {
  private String CURRENT_VERSION = "1.1";

  public ServerCommandsConfig(File path) {
    configPath = path;
  }

  private File configPath;

  public Properties getConfig() {
    if (!configPath.exists()) {
      ServerCommands.LOGGER.info("No configuration found for servercommands. Writing...");
      writeConfig(getDefaultConfig());
      ServerCommands.LOGGER.info("Configuration file written.");
    }

    return readConfig();
  }

  private void writeConfig(Properties prop) {
    try (OutputStream output = new FileOutputStream(configPath, false)) {

      // save properties to project root folder
      prop.store(output, "See https://github.com/AdamRaichu/server-side-commands/wiki/Configuration for more information");

      ServerCommands.LOGGER.info(prop.toString());

      output.close();

    } catch (IOException io) {
      io.printStackTrace();
    }
  }

  private Properties readConfig() {
    try (InputStream input = new FileInputStream(configPath)) {

      Properties prop = new Properties();

      // load a properties file
      prop.load(input);

      if (!(prop.getProperty("CONFIG_FILE_VERSION").equals(CURRENT_VERSION))) {
        prop = updateConfig(prop);
      }

      return prop;

    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return new Properties();
  }

  private Properties updateConfig(Properties old) {
    if (old.getProperty("CONFIG_FILE_VERSION").equals("1")) {
      old.setProperty("cmds.track.permissionLevel", "2");
      old.setProperty("CONFIG_FILE_VERSION", CURRENT_VERSION);
      ServerCommands.LOGGER.info("Updating config file from mod version 1.1.0 to 1.2.0.");
    } else {
      ServerCommands.LOGGER.error("`updateConfig` was called, but version was not known.");
      ServerCommands.LOGGER
          .error("Go to the link below for help with this error.");
      ServerCommands.LOGGER.error("https://github.com/AdamRaichu/server-side-commands/wiki/Errors#updateconfig-was-called-but-version-was-not-known");
    }

    writeConfig(old);

    return old;
  }

  private Properties getDefaultConfig() {
    Properties prop = new Properties();

    // set the default value
    prop.setProperty("CONFIG_FILE_VERSION", CURRENT_VERSION);
    prop.setProperty("cmds.freeze.permissionLevel", "2");
    prop.setProperty("cmds.freeze.logUsage", "true");
    prop.setProperty("cmds.track.permissionLevel", "2");

    return prop;
  }
}
