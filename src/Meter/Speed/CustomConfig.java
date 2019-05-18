package Meter.Speed;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {
	private FileConfiguration config = null;
	private final File configFile;
	private final String filename;
	private final SpeedMeter plugin;

	public CustomConfig(SpeedMeter plugin) {
		this(plugin, "playerdata.yml");
	}

	public CustomConfig(SpeedMeter plugin, String fileName) {
		this.plugin = plugin;
		this.filename = fileName;
		configFile = new File(plugin.getDataFolder(), filename);
	}

	public void saveDefaultConfig() {
		if (!configFile.exists()) {
			plugin.saveResource(filename, false);
		}
	}

	public void reloadConfig() {

		config = YamlConfiguration.loadConfiguration(configFile);
		final InputStream defConfigStream = plugin.getResource(filename);
		if (defConfigStream == null) {
			return;
		}

		config.setDefaults(YamlConfiguration.loadConfiguration(
				new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
	}

	public FileConfiguration getConfig() {
		if (config == null) {
			reloadConfig();
		}
		if (!(config == null)) {
		}
		return config;
	}

	public void saveConfig() {
		if (config == null) {
			return;
		}
		try {
			getConfig().save(configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
		}
	}
}
