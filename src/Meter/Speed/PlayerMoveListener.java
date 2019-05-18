package Meter.Speed;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import static Meter.Speed.SpeedMeter.*;

public class PlayerMoveListener implements Listener {

	private SpeedMeter plugin;
	static double playerSpeedThresholdValue = 15;
	static double otherVehicleSpeedThresholdValue = 30;
	static double vehicleSpeedThresholdValue = 0.3;
	static Boolean ElytraMode = true;

		PlayerMoveListener(SpeedMeter plugin) {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
			this.plugin = plugin;
		}

	private HashMap<UUID, MoveData> list = new HashMap<UUID, MoveData>();


		@EventHandler
		public void onMoveVehicle(final VehicleMoveEvent e) {

			if(!enablemeter) return;
			if(!scheduler && !(countmainasu >= 500)) return;
			Vehicle v = e.getVehicle();
			if(!((v instanceof Boat) || (v instanceof Minecart))) return;
			List<Entity> passengers = v.getPassengers();
			if(passengers.isEmpty() || !(passengers.get(0) instanceof Player)) return;
			Player p;
			p = (Player) passengers.get(0);
			if(!p.hasPermission("speed.meter.speedmeter.boat")) return;
		    if(!AllowedWorldNames.contains(p.getWorld().getName())) return;
			if(manager.discreteMeterOnOff.containsKey(p.getName()))
				if(!(manager.discreteMeterOnOff.get(p.getName()))) return;
			UUID uuid = p.getUniqueId();

			/*
			 * ボートの速度算出方法1
			*/

			if(!list.containsKey(uuid)){
				MoveData data = new MoveData();
				data.setTime(System.currentTimeMillis());
				data.setLocation(e.getFrom().getX(), e.getFrom().getZ());
				list.put(uuid, data);
				return;
			}

			MoveData data = list.get(uuid);
			double x = e.getFrom().getX();
			double z = e.getFrom().getZ();
			double d = getRange(data.getX() - x, data.getZ() - z);
			long currentTime = System.currentTimeMillis();
			double time = (currentTime - data.getTime()) / 1000D;
			double n = d / time;
			double velocity1 = n * 3600D / 1000D;
			data.setTime(currentTime);
			data.setLocation(x, z);
			/*
			 * ボートの速度算出方法2
			*/

			double Tx = e.getTo().getX();
			double Tz = e.getTo().getZ();
			double Fx = e.getFrom().getX();
			double Fz = e.getFrom().getZ();
			double x1 = Tx - Fx;
			double z1 = Tz - Fz;
			double x2 = Math.pow(x1, 2);
			double z2 = Math.pow(z1, 2);
			double leng = Math.sqrt(x2 + z2);
			double velocity2 = leng * 72000 / 1000;

			// メーターちらつき防止処理
			double speedThreshold = ((velocity1 + velocity2) / 2) * vehicleSpeedThresholdValue;
			double lowSpeedThreshold = 3;
			double gosa = velocity2 - velocity1;
			if (velocity1 <= 20 ) {
				if(!(normalize(gosa) > lowSpeedThreshold)) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
							String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
				}
				return;
			}
			else if(velocity1 > 20) {
				if(normalize(gosa) > speedThreshold) {
					return;
				}
				else {
					if(velocity1 < 150) {
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
								new TextComponent(ChatColor.AQUA  + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
										String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
					}
					else {
						if(velocity2 < 200) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									new TextComponent(ChatColor.AQUA  + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
											String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
						}
						else if(velocity2 >= 200) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
											String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
						}
					}
					return;
				}
			}
		}

		@EventHandler
		public void onPlayerMove(final PlayerMoveEvent e) {
			if(!enablemeter) return;
			if(!scheduler && (countmainasu >= 500) == false) return;
			Player p = e.getPlayer();
			if(p.isInsideVehicle()) {
				Entity v = p.getVehicle();
				if(((v instanceof Boat) || (v instanceof Minecart))) return;

				// ボート、トロッコ以外の乗り物の場合
				if(((v instanceof Horse) || (v instanceof Pig) || (v instanceof ChestedHorse) || (v instanceof Llama)
						|| (v instanceof ZombieHorse) || (v instanceof SkeletonHorse))) {
					List<Entity> passengers = v.getPassengers();
					if(passengers.isEmpty() || !(passengers.get(0) instanceof Player)) return;
					if(!p.hasPermission("speed.meter.speedmeter.boat")) return;
				    if(!AllowedWorldNames.contains(p.getWorld().getName())) return;
					if(manager.discreteMeterOnOff.containsKey(p.getName()))
						if(!(manager.discreteMeterOnOff.get(p.getName()))) return;
					UUID uuid = p.getUniqueId();

					/*
					 * ボートの速度算出方法1
					*/

					double velocity1 = velocityCalculation1(uuid, e, false);
					if(velocity1 == -1) return;

					/*
					 * 速度算出方法2
					*/

					double velocity2 = velocityCalculation2(e, false);
					setDisplayVelocity(velocity1, velocity2, p, otherVehicleSpeedThresholdValue);
					return;
				}
			}
			if(!plugin.allMeterOnOffList.containsKey(p.getName())) return; // 既定でメーターを表示しない
			if(!plugin.allMeterOnOffList.get(p.getName())) return;
			if(!p.hasPermission("speed.meter.speedmeter.allmeter")) return;
		    if(!AllowedWorldNames.contains(p.getWorld().getName())) return;
			if(manager.discreteMeterOnOff.containsKey(p.getName()))
				if(!(manager.discreteMeterOnOff.get(p.getName()))) return;
			UUID uuid = p.getUniqueId();

			if(ElytraMode) {
				// エリトラをつけていない場合は2Dモード
				if(!(p.getInventory().getChestplate() == null)) {
					if(!(p.getInventory().getChestplate().getType() == Material.ELYTRA)) {
						double velocity1 = velocityCalculation1(uuid, e, false);
						if(velocity1 == -1) return;

						double velocity2 = velocityCalculation2(e, false);
						setDisplayVelocity(velocity1, velocity2, p, playerSpeedThresholdValue);
						return;
					}
				}else if(p.getInventory().getChestplate() == null) {
					double velocity1 = velocityCalculation1(uuid, e, false);
					if(velocity1 == -1) return;

					double velocity2 = velocityCalculation2(e, false);
					setDisplayVelocity(velocity1, velocity2, p, playerSpeedThresholdValue);
					return;
				}
				else if(p.getInventory().getChestplate().getType() == Material.ELYTRA) {
					double velocity1 = velocityCalculation1(uuid, e, true);
					if(velocity1 == -1) return;

					double velocity2 = velocityCalculation2(e, true);
					setDisplayVelocity(velocity1, velocity2, p, playerSpeedThresholdValue);
					return;
				}

			}

			double velocity1 = velocityCalculation1(uuid, e, true);
			if(velocity1 == -1) return;

			double velocity2 = velocityCalculation2(e, true);
			setDisplayVelocity(velocity1, velocity2, p, playerSpeedThresholdValue);
			return;
		}


		private double velocityCalculation1(UUID uuid, PlayerMoveEvent e, Boolean threeAxis) {
			if(!list.containsKey(uuid)){
				MoveData data = new MoveData();
				data.setTime(System.currentTimeMillis());
				if(threeAxis) data.setLocation(e.getFrom().getX(), e.getFrom().getZ(), e.getFrom().getY());
				else if(!threeAxis) data.setLocation(e.getFrom().getX(), e.getFrom().getZ());
				list.put(uuid, data);
				return -1;
			}

			MoveData data = list.get(uuid);
			double x = e.getFrom().getX();
			double z = e.getFrom().getZ();
			double y = 0;
			if(threeAxis) {
				y = e.getFrom().getY();
			}
			double d = 0;
			if(threeAxis) {
				d = getRange(data.getX() - x, data.getZ() - z, data.getY() - y);
			}
			else if(!threeAxis) {
				d = getRange(data.getX() - x, data.getZ() - z);
			}
			long currentTime = System.currentTimeMillis();
			double time = (currentTime - data.getTime()) / 1000D;
			double n = d / time;
			double velocity1 = n * 3600D / 1000D;
			data.setTime(currentTime);
			if(threeAxis) {
				data.setLocation(x, z, y);
			}
			else {
				data.setLocation(x, z);
			}

			return velocity1;
		}

		private double velocityCalculation2(PlayerMoveEvent e, Boolean threeAxis) {
			double Tx = e.getTo().getX();
			double Tz = e.getTo().getZ();
			double Ty = 0;
			if(threeAxis)
				Ty = e.getTo().getY();
			double Fx = e.getFrom().getX();
			double Fz = e.getFrom().getZ();
			double Fy = 0;
			if(threeAxis)
				Fy = e.getFrom().getY();
			double x1 = Tx - Fx;
			double z1 = Tz - Fz;
			double y1 = 0;
			if(threeAxis)
				y1 = Ty - Fy;
			double x2 = Math.pow(x1, 2);
			double z2 = Math.pow(z1, 2);
			double y2 = 0;
			if(threeAxis)
				y2 = Math.pow(y1, 2);
			double leng = Math.sqrt(x2 + z2 + y2);
			double velocity2 = leng * 72000 / 1000;
			return velocity2;
		}

		private void setDisplayVelocity(double velocity1, double velocity2, Player p, double thresholdValue) {
			//　ちらつき防止処理
			double speedThreshold = ((velocity1 + velocity2) / 2) * thresholdValue;
			double lowSpeedThreshold = 1;
			double difference = velocity2 - velocity1;
			if (velocity1 <= 20 ) {
				if(!(normalize(difference) > lowSpeedThreshold)) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
							String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
				}
				return;
			}
			else if(velocity1 > 20) {
				if(normalize(difference) > speedThreshold) {
					return;
				}
				else {
					if(velocity1 < 150) {
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
								new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
										String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
					}
					else {
						if(velocity2 < 200) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
											String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
						}
						else if(velocity2 >= 200 && velocity2 < 800) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
											String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
						}
						else if(velocity2 >= 800) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
											String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
						}
					}
					return;
				}
			}
		}

		private static double getRange(double a, double b, double c){
			double d = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(b, 2));
			return d;
		}

		private static double getRange(double a, double b){
			double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
			return c;
		}

		private static double normalize(double d){
			if(d < 0){
			d = -d;
			}
			return d;
			}
}
