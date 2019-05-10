package Meter.Speed;

import java.net.MalformedURLException;
import java.net.URL;
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

	@Override
	public void onEnable() {
		// TODO 自動生成されたメソッド・スタブ
		super.onEnable();
		new PlayerMoveListener(this);
		System.out.println("\u001b[32m" + "SpeedMeterがロードされました" + "\u001b[m");

		// config
		initialize();

		task = new TaskControl().runTaskTimer(this, 0, 1);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		/**
		 *  AdminCommands
		 */
		if (command.getName().equalsIgnoreCase("speedmeter")) {
			// 権限をチェック
			if (!sender.hasPermission("speed.meter.command.speedmeter")) {
				sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
				displayInfo(sender);
				return true;
			}
			switch (args.length) {
			case 0:
				if (SpeedMeter.enablemeter == true) {
					sender.sendMessage(ChatColor.GRAY + "SpeedMeterは有効です");
					displayInfo(sender);
					return true;
				} else if (SpeedMeter.enablemeter == false) {
					sender.sendMessage(ChatColor.GRAY + "SpeedMeterは無効です");
					displayInfo(sender);
					return true;
				}
				return false;
			case 1:
				if (args[0].equalsIgnoreCase("true")) {
					SpeedMeter.enablemeter = true;
					sender.sendMessage(ChatColor.AQUA + "SpeedMeterは有効になりました");
					FileConfiguration config = this.getConfig();
					config.set("enablemeter", true);
					saveConfig();
					reloadConfig();
					return true;

				} else if (args[0].equalsIgnoreCase("false")) {
					SpeedMeter.enablemeter = false;
					sender.sendMessage(ChatColor.GRAY + "SpeedMeterは無効になりました");
					FileConfiguration config = this.getConfig();
					config.set("enablemeter", false);
					this.saveConfig();
					this.reloadConfig();
					return true;
				} else if (args[0].equalsIgnoreCase("commands")) {
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "- SpeedMeter - コマンド一覧");
					return false;
				} else if (args[0].equalsIgnoreCase("reload")) {
					initialize();
					sender.sendMessage(ChatColor.GRAY + "全ての設定値をリロードしました。");
					return true;
				} else if (args[0].equalsIgnoreCase("r")) {
					sender.sendMessage(ChatColor.GRAY + String.valueOf(countmainasu) + "/500に設定されています");
					return true;
				} else if (args[0].equalsIgnoreCase("addmeter")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED + "ゲーム内から実行してください。");
						return true;
					}
					Player player = (Player) sender;
					World world = player.getWorld();
					String name = world.getName();
					if (!this.config.contains("Allowed_World_Names")) {
						AllowedWorldNames.add(name);
						FileConfiguration config = this.getConfig();
						config.set("Allowed_World_Names", AllowedWorldNames);
						sender.sendMessage(ChatColor.GRAY + "ワールド " + name + " でSpeedMeterが有効になりました。");
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "worldlist: "
								+ config.getList("Allowed_World_Names"));
						this.saveConfig();
						this.reloadConfig();
						return true;
					}
					AllowedWorldNames.add(name);
					FileConfiguration config = this.getConfig();
					config.set("Allowed_World_Names", AllowedWorldNames);
					sender.sendMessage(ChatColor.GRAY + "ワールド " + name + " でSpeedMeterが有効になりました。");
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + config.getList("Allowed_World_Names"));
					saveConfig();
					this.reloadConfig();
					return true;
				} else if (args[0].equalsIgnoreCase("removemeter")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED + "ゲーム内から実行してください。");
						return true;
					}
					Player player = (Player) sender;
					World world = player.getWorld();
					String name = world.getName();
					if (!this.config.contains("Allowed_World_Names")) {
						if (AllowedWorldNames.remove(name)) {
							FileConfiguration config = this.getConfig();
							config.set("Allowed_World_Names", AllowedWorldNames);
							sender.sendMessage(ChatColor.GRAY + "ワールド " + name + " でSpeedMeterが無効になりました。");
							sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "worldlist: "
									+ config.getList("Allowed_World_Names"));
							this.saveConfig();
							this.reloadConfig();
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "削除できないか、登録されていません。");
							return true;
						}
					} else if (AllowedWorldNames.remove(name)) {
						FileConfiguration config = this.getConfig();
						config.set("Allowed_World_Names", null);
						saveConfig();
						config.set("Allowed_World_Names", AllowedWorldNames);
						sender.sendMessage(ChatColor.GRAY + "ワールド " + name + " でSpeedMeterが無効になりました。");
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "worldlist: "
								+ config.getList("Allowed_World_Names"));
						saveConfig();
						this.reloadConfig();
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "削除できないか、登録されていません。");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("worldlist")) {
					FileConfiguration config = this.getConfig();
					if (!config.contains("Allowed_World_Names")) {
						sender.sendMessage(ChatColor.RED + "config.ymlにAllowed_World_Names:がみつかりません。");
						return true;
					}
					sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "worldlist: "
							+ config.getList("Allowed_World_Names"));
					return true;
				} else {
					return false;
				}
			case 2:
				if (args[0].equalsIgnoreCase("r")) {
					countmainasu = Integer.valueOf(args[1]);
					FileConfiguration config = this.getConfig();
					config.set("materfrequency", Integer.valueOf(args[1]));
					// config.options().header("スピード取得頻度！(1 ~ 20)\n default: 20");
					saveConfig();
					this.reloadConfig();
					sender.sendMessage(ChatColor.AQUA + config.getString("materfrequency") + "/500に設定されました");
					return true;
				} else if (args[0].equalsIgnoreCase("elytra")) {
					if (args[1].equalsIgnoreCase("true")) {
						FileConfiguration config = getConfig();
						config.set("Calculate_speed_in_three_dimensions_Elytra", true);
						saveConfig();
						this.reloadConfig();
						PlayerMoveListener.ElytraMode = true;
						sender.sendMessage(ChatColor.GRAY + "エリトラ装着時はy軸方向（落下、上昇スピード）を速度に含めます（乗り物乗車時を除く）。");
						sender.sendMessage(ChatColor.GRAY + "エリトラ非装着時はy軸方向を速度に含めません。");
						return true;
					} else if (args[1].equalsIgnoreCase("false")) {
						FileConfiguration config = getConfig();
						config.set("Calculate_speed_in_three_dimensions_Elytra", false);
						saveConfig();
						this.reloadConfig();
						PlayerMoveListener.ElytraMode = false;
						sender.sendMessage(ChatColor.GRAY + "乗り物乗車時を除くすべてでy軸方向（落下、上昇スピード）を速度に含めます。");
						return true;
					}
					return false;
				} else if (args[0].equalsIgnoreCase("pst")) {
					FileConfiguration config = getConfig();
					config.set("Player_Speed_Threshold", args[1]);
					saveConfig();
					this.reloadConfig();
					PlayerMoveListener.playerSpeedThresholdValue = Double.parseDouble(args[1]);
					sender.sendMessage(ChatColor.GRAY + "playerSpeedThresholdValue:"
							+ config.getDouble("playerSpeedThresholdValue"));
					return true;
				} else if (args[0].equalsIgnoreCase("ovst")) {
					FileConfiguration config = getConfig();
					config.set("Other_Vehicle_Speed_Threshold", args[1]);
					saveConfig();
					this.reloadConfig();
					PlayerMoveListener.otherVehicleSpeedThresholdValue = Double.parseDouble(args[1]);
					sender.sendMessage(ChatColor.GRAY + "Other_Vehicle_Speed_Threshold:"
							+ config.getDouble("Other_Vehicle_Speed_Threshold"));
				} else if (args[0].equalsIgnoreCase("pst")) {
					FileConfiguration config = getConfig();
					config.set("Vehicle_Speed_Threshold", args[1]);
					saveConfig();
					this.reloadConfig();
					PlayerMoveListener.vehicleSpeedThresholdValue = Double.parseDouble(args[1]);
					sender.sendMessage(
							ChatColor.GRAY + "Vehicle_Speed_Threshold:" + config.getDouble("Vehicle_Speed_Threshold"));
					return true;
				} else {
					return false;
				}
			default:
				break;
			}
			// TODO 自動生成されたメソッド・スタブ
			return super.onCommand(sender, command, label, args);
		}

		/**
		 *  MainCommands
		 */
		else if (command.getName().equalsIgnoreCase("meter")) {
			if (args.length == 0) {
				Player player = (Player) sender;
				if (SpeedMeter.manager.discreteMeterOnOff.containsKey(player.getName())) {
					if (!(SpeedMeter.manager.discreteMeterOnOff.get(player.getName()))) {
						manager.setPlayersDiscreteDeteronoff(player.getName(), true);
						if (allMeterOnOffList.get(player.getName())) {
							sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "info: 常に速度が表示されます。");
							return true;
						} else if (!allMeterOnOffList.get(player.getName())
								|| !allMeterOnOffList.containsKey(player.getName())) {
							sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "info: 乗り物に乗っているときにだけ速度が表示されます。");
							return true;
						}
					}
					if ((SpeedMeter.manager.discreteMeterOnOff.get(player.getName()))) {
						manager.setPlayersDiscreteDeteronoff(player.getName(), false);
						sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "info: 速度は非表示です。");
						return true;
					}
				}
			} else if (args.length == 1 && ((args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")))) {
				// meterのon/off の実行

				if (!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player) sender;

				// meterをon/offにする
				boolean value = args[0].equalsIgnoreCase("on");
				manager.setPlayersDiscreteDeteronoff(player.getName(), value);
				if (value) {
					if (SpeedMeter.manager.discreteMeterOnOff.containsKey(player.getName()))
						if ((SpeedMeter.manager.discreteMeterOnOff.get(player.getName()))) {
							if (allMeterOnOffList.get(player.getName())
									|| !allMeterOnOffList.containsKey(player.getName())) {
								sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "info: 常に速度が表示されます。");
								sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD +
										"Tip: " + ChatColor.GRAY + "" + ChatColor.BOLD
										+ "/allmeter off - 乗り物に乗っているときにだけ速度を表示します。");
							} else if (!allMeterOnOffList.get(player.getName())
									|| !allMeterOnOffList.containsKey(player.getName())) {
								sender.sendMessage(
										ChatColor.AQUA + "" + ChatColor.BOLD + "info: 乗り物に乗ってるときにだけ速度を表示します。");
								sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD +
										"Tip: " + ChatColor.GRAY + "" + ChatColor.BOLD
										+ "/allmeter on - 常に速度を表示し続けます。");
							}
						}
				} else {
					if (SpeedMeter.manager.discreteMeterOnOff.containsKey(player.getName())) {
						if ((!SpeedMeter.manager.discreteMeterOnOff.get(player.getName()))) {
							if (allMeterOnOffList.containsKey(player.getName())
									|| !allMeterOnOffList.containsKey(player.getName())) {
								if (!allMeterOnOffList.get(player.getName())
										|| allMeterOnOffList.get(player.getName()) == null) {
									sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "info: 速度は非表示です。");
									return true;
								} else if (allMeterOnOffList.get(player.getName())) {
									sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "info: 速度は非表示です。");
									sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD +
											"Tip: " + ChatColor.GRAY + "" + ChatColor.BOLD
											+ "もう一度 /meter on で常に速度が表示されます。");
									return true;
								}
							}
						}
					}
				}
			}
			return true;
		}

		else if (command.getName().equalsIgnoreCase("allmeter")) {
			if (!sender.hasPermission("speed.meter.speedmeter.allmeter")) {
				sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
				return true;
			}
			if (args.length == 0) {
				Player player = (Player) sender;
				if (!this.allMeterOnOffList.get(player.getName())
						|| !this.allMeterOnOffList.containsKey(player.getName())) {
					return setAllmeter(true, sender);
				}
				if (this.allMeterOnOffList.get(player.getName())) {
					return setAllmeter(false, sender);
				}
			}
			if (args.length == 1 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
				// meterのon/off の実行
				if (!(sender instanceof Player))
					return true;
				if (args[0].equalsIgnoreCase("on")) {
					return setAllmeter(true, sender);
				} else if (args[0].equalsIgnoreCase("off")) {
					return setAllmeter(false, sender);
				}
				return true;
			}
		}
		return false;
	}

	public Boolean setAllmeter(Boolean b, CommandSender sender) {
		Player player = (Player) sender;
		if (b) {
			if (SpeedMeter.manager.discreteMeterOnOff.get(player.getName())) {
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "info: 常に速度が表示されます。");
				allMeterOnOffList.put(player.getName(), true);
				playerDataConfig.set(player.getName(), true);
				playerDataCustomconfig.saveConfig();
				return true;
			} else if (!SpeedMeter.manager.discreteMeterOnOff.get(player.getName())) {
				manager.setPlayersDiscreteDeteronoff(player.getName(), true);
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "info: 常に速度が表示されます。");
				allMeterOnOffList.put(player.getName(), true);
				playerDataConfig.set(player.getName(), true);
				playerDataCustomconfig.saveConfig();
				return true;
			}
		} else {
			sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "info: 乗り物に乗ってるときにだけ速度を表示します。");
			allMeterOnOffList.put(player.getName(), false);
			playerDataConfig.set(player.getName(), false);
			playerDataCustomconfig.saveConfig();
			return true;
		}
		return false;
	}

	public void reloadPlayerData() {
		playerDataConfig = playerDataCustomconfig.getConfig();
		for (String playerName : playerDataConfig.getKeys(false)) {
			allMeterOnOffList.put(playerName, playerDataConfig.getBoolean(playerName));
		}
		return;
	}

	public static SpeedMeter getInstance() {
		if (instance == null) {
			instance = (SpeedMeter) Bukkit.getPluginManager().getPlugin("SpeedMeter");
		}
		return instance;
	}

	public void info(String s) {
		getLogger().info(s);
	}

	public URL getSiteURL() {
		URL url = null;
		try {
			url = new URL("https://github.com/rnlin430/SpeedMeter/releases");
		} catch (MalformedURLException e) {
			info(ChatColor.GRAY + "未設定です。");
		}
		return url;
	}

	public void initialize() {
		// config
		saveDefaultConfig();
		reloadConfig();
		config = getConfig();
		enablemeter = config.getBoolean("enablemeter");
		countmainasu = config.getInt("meterfrequency");
		AllowedWorldNames = config.getStringList("Allowed_World_Names");
		this.reloadConfig();
		PlayerMoveListener.playerSpeedThresholdValue = config.getDouble("Player_Speed_Threshold",
				PlayerMoveListener.playerSpeedThresholdValue);
		PlayerMoveListener.otherVehicleSpeedThresholdValue = config.getDouble("Other_Vehicle_Speed_Threshold",
				PlayerMoveListener.otherVehicleSpeedThresholdValue);
		PlayerMoveListener.vehicleSpeedThresholdValue = config.getDouble("Vehicle_Speed_Threshold",
				PlayerMoveListener.vehicleSpeedThresholdValue);
		manager = new Manager();
		manager.reloadAllData();

		playerDataCustomconfig = new CustomConfig(this, "playerdata.yml");
		playerDataCustomconfig.saveDefaultConfig();
		this.reloadPlayerData();
	}

	public void displayInfo(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "- SpeedMeter -");
		sender.sendMessage(ChatColor.WHITE + "Spigotバージョン : 1.13.2");
		sender.sendMessage(ChatColor.WHITE + "Pluginバージョン : " + getDescription().getVersion());
		sender.sendMessage(ChatColor.YELLOW + "ダウンロードURL : " + getSiteURL());
		sender.sendMessage(ChatColor.YELLOW + "コマンド一覧 : " + "/speedmeter commands");
		sender.sendMessage(ChatColor.DARK_BLUE + "Developed by rnlin(Twitter: @rnlin)");
		sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "--------");
	}
}
