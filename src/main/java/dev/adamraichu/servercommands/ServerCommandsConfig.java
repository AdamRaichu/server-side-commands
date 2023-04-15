package dev.adamraichu.servercommands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ServerCommandsConfig {
  public ServerCommandsConfig(File path) {
    configPath = path;
  }

  private File configPath;

  public Properties getConfig() {
    if (!configPath.exists()) {
      ServerCommands.LOGGER.info("No configuration found for servercommands. Writing...");
      writeConfig();
    }

    return readConfig();
  }

  private void writeConfig() {
    try (OutputStream output = new FileOutputStream(configPath)) {

      Properties prop = new Properties();

      // set the default value
      prop.setProperty("cmds.freeze.permissionLevel", "2");
      prop.setProperty("cmds.freeze.logUsage", "true");

      // save properties to project root folder
      prop.store(output, null);

      System.out.println(prop);

    } catch (IOException io) {
      io.printStackTrace();
    }
  }

  private Properties readConfig() {
    try (InputStream input = new FileInputStream(configPath)) {

      Properties prop = new Properties();

      // load a properties file
      prop.load(input);

      return prop;

    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return new Properties();
  }
}
