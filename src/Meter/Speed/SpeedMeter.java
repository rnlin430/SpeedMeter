package Meter.Speed;


import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SpeedMeter extends JavaPlugin {


	@Override
	public void onDisable() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDisable();
	}

	BukkitTask task;
	static public boolean enablemeter = true;
	public static int countmainasu = 300;
    public static Boolean scheduler = true;
    public static List<String> AllowedWorldNames;
	public static Manager manager;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		// TODO 自動生成されたメソッド・スタブ
		super.onEnable();
		new VehicleListener(this);
		System.out.println("\u001b[32m" + "SpeedMeterがロードされました" + "\u001b[m");

		// config
		saveDefaultConfig();
		FileConfiguration config = getConfig();

		enablemeter = config.getBoolean("enablemeter");
		countmainasu = config.getInt("meterfrequency");
		AllowedWorldNames = config.getStringList("Allowed_World_Names");
		this.reloadConfig();

		manager = new Manager();
		manager.reloadAllData();

		task = this.getServer().getScheduler().runTaskTimer(this, new TaskControl(), 0L, 1L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("speedmeter")) {
			// 権限をチェック
			if(!sender.hasPermission("speed.meter.command.speedmeter")) {
				sender.sendMessage(ChatColor.GRAY + command.getPermissionMessage());
				return true;
			}
			switch(args.length) {
			case 0:
				if(SpeedMeter.enablemeter == true) {
					sender.sendMessage(ChatColor.GRAY + "SpeedMeterは有効です");
					return false;
				}
				else if(SpeedMeter.enablemeter == false){
					sender.sendMessage(ChatColor.GRAY + "SpeedMeterは無効です");
					return false;
				}
				return false;
			case 1:
				if(args[0].equalsIgnoreCase("true")){
					SpeedMeter.enablemeter = true;
					sender.sendMessage(ChatColor.AQUA + "SpeedMeterは有効になりました");
					FileConfiguration config = this.getConfig();
					config.set("enablemeter", true);
					saveConfig();
					return true;

				}
				else if(args[0].equalsIgnoreCase("false")) {
					SpeedMeter.enablemeter = false;
					sender.sendMessage(ChatColor.GRAY + "SpeedMeterは無効になりました");
					FileConfiguration config = this.getConfig();
					config.set("enablemater", false);
					saveConfig();
					return true;
				}
				else if(args[0].equalsIgnoreCase("r")) {
					sender.sendMessage(ChatColor.GRAY + String.valueOf(countmainasu) + "/500に設定されています");
					return true;
				}
				else {
					return false;
				}

			case 2:
				if(args[0].equalsIgnoreCase("r")) {
					countmainasu = Integer.valueOf(args[1]);
					FileConfiguration config = this.getConfig();
					config.set("materfrequency", Integer.valueOf(args[1]));
					// config.options().header("スピード取得頻度！(1 ~ 20)\n default: 20");
					saveConfig();
					sender.sendMessage(ChatColor.AQUA + config.getString("materfrequency") + "/500に設定されました");
					return true;
				}

				else {
					return false;
				}

			default:
				break;
			}
			// TODO 自動生成されたメソッド・スタブ
			return super.onCommand(sender, command, label, args);
		}

		else if (command.getName().equalsIgnoreCase("meter")) {
			if ( args.length == 1 &&(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) ) {
				// meterのon/off の実行

				if (!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player)sender;

				// meterをon/offにする
				boolean value = args[0].equalsIgnoreCase("on");
				manager.setPlayersDiscreteDeteronoff(player.getName(), value);
				return true;
			}
		}
		return false;
	}

    private static SpeedMeter instance;
    public static SpeedMeter getInstance() {
        if ( instance == null ) {
            instance = (SpeedMeter)Bukkit.getPluginManager().getPlugin("SpeedMeter");
        }
        return instance;
    }
}
