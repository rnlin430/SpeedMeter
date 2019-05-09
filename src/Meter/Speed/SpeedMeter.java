package Meter.Speed;


import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
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
	private static SpeedMeter instance;
	static public boolean enablemeter = true;
	public static int countmainasu = 300;
    public static Boolean scheduler = true;
    public static List<String> AllowedWorldNames;
	public static Manager manager;
	FileConfiguration config;
	FileConfiguration playerDataConfig;
	CustomConfig playerDataCustomconfig;
	public HashMap<String, Boolean> allMeterOnOffList = new HashMap<String, Boolean>();
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		// TODO 自動生成されたメソッド・スタブ
		super.onEnable();
		new VehicleListener(this);
		System.out.println("\u001b[32m" + "SpeedMeterがロードされました" + "\u001b[m");

		// config
		saveDefaultConfig();
		config = getConfig();

		enablemeter = config.getBoolean("enablemeter");
		countmainasu = config.getInt("meterfrequency");
		AllowedWorldNames = config.getStringList("Allowed_World_Names");
		this.reloadConfig();

		manager = new Manager();
		manager.reloadAllData();

		playerDataCustomconfig = new CustomConfig(this, "playerdata.yml");
		playerDataCustomconfig.saveDefaultConfig();
		this.reloadPlayerData();

		task = this.getServer().getScheduler().runTaskTimer(this, new TaskControl(), 0L, 1L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("speedmeter")) {
			// 権限をチェック
			if(!sender.hasPermission("speed.meter.command.speedmeter")) {
				sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
				return true;
			}
			switch(args.length) {
			case 0:
				if(SpeedMeter.enablemeter == true) {
					sender.sendMessage(ChatColor.GRAY + "SpeedMeterは有効です");
					return true;
				}
				else if(SpeedMeter.enablemeter == false){
					sender.sendMessage(ChatColor.GRAY + "SpeedMeterは無効です");
					return true;
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
					config.set("enablemeter", false);
					this.saveConfig();
					return true;
				}
				else if(args[0].equalsIgnoreCase("r")) {
					sender.sendMessage(ChatColor.GRAY + String.valueOf(countmainasu) + "/500に設定されています");
					return true;
				}
				else if(args[0].equalsIgnoreCase("addmeter")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED + "ゲーム内から実行してください。");return true;}
					Player player = (Player)sender;
					World world = player.getWorld();
					String name = world.getName();
					if(!this.config.contains("Allowed_World_Names")) {
						AllowedWorldNames.add(name);
						FileConfiguration config = this.getConfig();
						config.set("Allowed_World_Names", AllowedWorldNames);
						sender.sendMessage(ChatColor.GRAY + "ワールド " + name + " でSpeedMeterが有効になりました。");
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "worldlist: " + config.getList("Allowed_World_Names"));
						this.saveConfig();
						return true;
					}
					AllowedWorldNames.add(name);
					FileConfiguration config = this.getConfig();
					config.set("Allowed_World_Names", AllowedWorldNames);
					sender.sendMessage(ChatColor.GRAY + "ワールド " + name + " でSpeedMeterが有効になりました。");
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + config.getList("Allowed_World_Names"));
					saveConfig();
					return true;
				}
				else if(args[0].equalsIgnoreCase("removemeter")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED + "ゲーム内から実行してください。");return true;}
					Player player = (Player)sender;
					World world = player.getWorld();
					String name = world.getName();
					if(!this.config.contains("Allowed_World_Names")) {
						if(AllowedWorldNames.remove(name)) {
							FileConfiguration config = this.getConfig();
							config.set("Allowed_World_Names", AllowedWorldNames);
							sender.sendMessage(ChatColor.GRAY + "ワールド " + name + " でSpeedMeterが無効になりました。");
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "worldlist: " + config.getList("Allowed_World_Names"));
							this.saveConfig();
							System.out.println("1");
							return true;
						}
						else {
							sender.sendMessage(ChatColor.RED + "削除できないか、登録されていません。");
							return true;
						}
					}
					else if(AllowedWorldNames.remove(name)) {
						FileConfiguration config = this.getConfig();
						config.set("Allowed_World_Names", null);
						saveConfig();
						config.set("Allowed_World_Names", AllowedWorldNames);
						sender.sendMessage(ChatColor.GRAY + "ワールド " + name + " でSpeedMeterが無効になりました。");
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "worldlist: " + config.getList("Allowed_World_Names"));
						saveConfig();
						return true;
					}
					else {
						sender.sendMessage(ChatColor.RED + "削除できないか、登録されていません。");
					}
					return true;
				}
				else if(args[0].equalsIgnoreCase("worldlist")) {
					FileConfiguration config = this.getConfig();
					if(!config.contains("Allowed_World_Names")) {
						sender.sendMessage(ChatColor.RED + "config.ymlにAllowed_World_Names:がみつかりません。");
						return true;
					}
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "worldlist: " + config.getList("Allowed_World_Names"));
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
			if (args.length == 0) {
				Player player = (Player)sender;
				if(SpeedMeter.manager.discreteMeterOnOff.containsKey(player.getName())) {
					if(!(SpeedMeter.manager.discreteMeterOnOff.get(player.getName()))) {
						manager.setPlayersDiscreteDeteronoff(player.getName(), true);
						if(allMeterOnOffList.get(player.getName())) {
							sender.sendMessage(ChatColor.AQUA +  "" + ChatColor.BOLD + "常に速度が表示されます。");
							return true;
						}
						else if(!allMeterOnOffList.get(player.getName()) || !allMeterOnOffList.containsKey(player.getName())){
							sender.sendMessage(ChatColor.AQUA +  "" + ChatColor.BOLD + "乗り物に乗っているときにだけ速度が表示されます。");
							return true;
						}
					}
				} else if((SpeedMeter.manager.discreteMeterOnOff.get(player.getName()))) {
					manager.setPlayersDiscreteDeteronoff(player.getName(), false);
					sender.sendMessage(ChatColor.BLACK + "" + ChatColor.BOLD + "速度は非表示です。");
					return true;
				}
			}
			else if (args.length == 1 &&(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
				// meterのon/off の実行

				if (!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player)sender;

				// meterをon/offにする
				boolean value = args[0].equalsIgnoreCase("on");
				manager.setPlayersDiscreteDeteronoff(player.getName(), value);
				if(value) {
					if(SpeedMeter.manager.discreteMeterOnOff.containsKey(player.getName()))
						if((SpeedMeter.manager.discreteMeterOnOff.get(player.getName()))) {
							if(allMeterOnOffList.get(player.getName()) || !allMeterOnOffList.containsKey(player.getName())) {
								sender.sendMessage(ChatColor.AQUA +  "" + ChatColor.BOLD + "常に速度が表示されます。");
								sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD +
										"Tip: /allmeter off - 乗り物に乗っているときにだけ速度を表示します。");
							}
							else if(!allMeterOnOffList.get(player.getName()) || !allMeterOnOffList.containsKey(player.getName())) {
								sender.sendMessage(ChatColor.AQUA +  "" + ChatColor.BOLD + "乗り物に乗っているときにだけ速度が表示されます。");
								sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD +
										"Tip: /allmeter on - 常に速度を表示し続けます。");
							}
						}
				} else {
					if(SpeedMeter.manager.discreteMeterOnOff.containsKey(player.getName())) {
						if((!SpeedMeter.manager.discreteMeterOnOff.get(player.getName()))) {
							if(allMeterOnOffList.containsKey(player.getName())) {
								if(allMeterOnOffList.get(player.getName())) {
									sender.sendMessage(ChatColor.BLACK + "" + ChatColor.BOLD + "速度は非表示です。");
								}
							}
						}
					}
				}
			}
			return true;
		}

		else if (command.getName().equalsIgnoreCase("allmeter")) {
			if ( args.length == 1 &&(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) ) {
				// meterのon/off の実行

				if (!(sender instanceof Player)) {
					return true;
				}
				if(!sender.hasPermission("speed.meter.speedmeter.allmeter")) {
					sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
					return true;
				}
				Player player = (Player)sender;

				if(args[0].equalsIgnoreCase("on")) {
					sender.sendMessage(ChatColor.AQUA +  "" + ChatColor.BOLD + "常に速度が表示されます。");
					allMeterOnOffList.put(player.getName(), true);
					playerDataConfig.set(player.getName(), true);
					playerDataCustomconfig.saveConfig();
				}
				else if(args[0].equalsIgnoreCase("off")) {
					sender.sendMessage(ChatColor.AQUA +  "" + ChatColor.BOLD + "乗り物に乗ってるのときにだけ速度は表示されます。");
					allMeterOnOffList.put(player.getName(), false);
					playerDataConfig.set(player.getName(), false);
					playerDataCustomconfig.saveConfig();
				}
				return true;
			}
		}
		return false;
	}
	public void reloadPlayerData() {
		playerDataConfig = playerDataCustomconfig.getConfig();
		for(String playerName: playerDataConfig.getKeys(false)) {
			allMeterOnOffList.put(playerName, playerDataConfig.getBoolean(playerName));
		}
		return;
	}
    public static SpeedMeter getInstance() {
        if ( instance == null ) {
            instance = (SpeedMeter)Bukkit.getPluginManager().getPlugin("SpeedMeter");
        }
        return instance;
    }
}
