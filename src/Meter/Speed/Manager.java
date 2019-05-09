package Meter.Speed;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

public class Manager {

	private File fileDiscreteMeterOnOff;
	HashMap<String, Boolean> discreteMeterOnOff;


	public void reloadAllData() {

		fileDiscreteMeterOnOff = new File(SpeedMeter.getInstance().getDataFolder(), "data.yml");

		if ( !fileDiscreteMeterOnOff.exists() ) {
			YamlConfiguration conf = new YamlConfiguration();
			try {
				conf.save(fileDiscreteMeterOnOff);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration config_discrete_meter_on_off =
				YamlConfiguration.loadConfiguration(fileDiscreteMeterOnOff);

		discreteMeterOnOff = new HashMap<String, Boolean>();

		for ( String key : config_discrete_meter_on_off.getKeys(false) ) {
			discreteMeterOnOff.put(key, config_discrete_meter_on_off.getBoolean(key));
		}
	}

	private boolean saveDiscreteMeterOnOff() {
        try {
            YamlConfiguration config = new YamlConfiguration();

            for ( String key : discreteMeterOnOff.keySet() ) {
                config.set(key, discreteMeterOnOff.get(key));
            }

            config.save(fileDiscreteMeterOnOff);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setPlayersDiscreteDeteronoff(String player_name, boolean do_meter) {
    	discreteMeterOnOff.put(player_name, do_meter);
        saveDiscreteMeterOnOff();
    }
}