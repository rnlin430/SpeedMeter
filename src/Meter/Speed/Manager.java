package Meter.Speed;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

public class Manager {

	private File file_discrete_meter_on_off;
	HashMap<String, Boolean> discrete_meter_on_off;


	public void reloadAllData() {

		file_discrete_meter_on_off = new File(SpeedMeter.getInstance().getDataFolder(), "data.yml");

		if ( !file_discrete_meter_on_off.exists() ) {
			YamlConfiguration conf = new YamlConfiguration();
			try {
				conf.save(file_discrete_meter_on_off);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration config_discrete_meter_on_off =
				YamlConfiguration.loadConfiguration(file_discrete_meter_on_off);

		discrete_meter_on_off = new HashMap<String, Boolean>();

		for ( String key : config_discrete_meter_on_off.getKeys(false) ) {
			discrete_meter_on_off.put(key, config_discrete_meter_on_off.getBoolean(key));
		}
	}

	private boolean saveDiscreteMeterOnOff() {
        try {
            YamlConfiguration config = new YamlConfiguration();

            for ( String key : discrete_meter_on_off.keySet() ) {
                config.set(key, discrete_meter_on_off.get(key));
            }

            config.save(file_discrete_meter_on_off);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setPlayersDiscreteDeteronoff(String player_name, boolean do_meter) {
    	discrete_meter_on_off.put(player_name, do_meter);
        saveDiscreteMeterOnOff();
    }
}