package Meter.Speed;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
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

public class VehicleListener implements Listener {

	SpeedMeter plugin;
		public VehicleListener(SpeedMeter plugin) {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
			this.plugin = plugin;
		}

	private HashMap<UUID, MoveData> list = new HashMap<UUID, MoveData>();


		@EventHandler
		public void onMove(VehicleMoveEvent e) {

			if(SpeedMeter.enablemeter == false) return;
			if(SpeedMeter.scheduler == false && (SpeedMeter.countmainasu >= 500) == false) return;
			Vehicle v = e.getVehicle();
			if(!((v instanceof Boat) || (v instanceof Minecart) || (v instanceof Horse) || (v instanceof Pig)
					|| (v instanceof ChestedHorse) || (v instanceof Llama) || (v instanceof ZombieHorse)
					|| (v instanceof SkeletonHorse))) return;
			List<Entity> passengers = v.getPassengers();
			if(passengers.isEmpty() || !(passengers.get(0) instanceof Player)) return;
			Player p = (Player) passengers.get(0);
			if(!p.hasPermission("speed.meter.speedmeter.boat")) return;
		    if(!SpeedMeter.AllowedWorldNames.contains(p.getWorld().getName())) return;
			if(SpeedMeter.manager.discreteMeterOnOff.containsKey(p.getName()))
				if(!(SpeedMeter.manager.discreteMeterOnOff.get(p.getName()))) return;
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
			double speedThreshold = ((velocity1 + velocity2) / 2) * 0.3;
			double lowSpeedThreshold = 3;
			double gosa = velocity2 - velocity1;
			if (velocity1 <= 20 ) {
				if(!(normalize(gosa) > lowSpeedThreshold)) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
							String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
				}
				data.setTime(currentTime);
				data.setLocation(x, z);
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
					data.setTime(currentTime);
					data.setLocation(x, z);
					return;
				}
			}
		}


		@EventHandler
		public void onPlayerMove(PlayerMoveEvent e) {
			if(SpeedMeter.enablemeter == false) return;
			if(SpeedMeter.scheduler == false && (SpeedMeter.countmainasu >= 500) == false) return;
			Player p = e.getPlayer();
			if(p.isInsideVehicle()) {
				Entity v = p.getVehicle();
				if(((v instanceof Boat) || (v instanceof Minecart) || (v instanceof Horse) || (v instanceof Pig)
						|| (v instanceof ChestedHorse) || (v instanceof Llama) || (v instanceof ZombieHorse)
						|| (v instanceof SkeletonHorse))) return;
			}
			if(!plugin.allMeterOnOffList.containsKey(p.getName())) return; // 既定でメーターを表示しない
			if(!plugin.allMeterOnOffList.get(p.getName())) return;
			if(!p.hasPermission("speed.meter.speedmeter.allmeter")) return;
		    if(!SpeedMeter.AllowedWorldNames.contains(p.getWorld().getName())) return;
			if(SpeedMeter.manager.discreteMeterOnOff.containsKey(p.getName()))
				if(!(SpeedMeter.manager.discreteMeterOnOff.get(p.getName()))) return;
			UUID uuid = p.getUniqueId();

			/*
			 * ボートの速度算出方法1
			*/

			if(!list.containsKey(uuid)){
				MoveData data = new MoveData();
				data.setTime(System.currentTimeMillis());
				data.setLocation(e.getFrom().getX(), e.getFrom().getZ(), e.getFrom().getY());
				list.put(uuid, data);
				return;
			}

			MoveData data = list.get(uuid);
			double x = e.getFrom().getX();
			double z = e.getFrom().getZ();
			double y = e.getFrom().getY();
			double d = getRange(data.getX() - x, data.getZ() - z, data.getY() - y);
			long currentTime = System.currentTimeMillis();
			double time = (currentTime - data.getTime()) / 1000D;
			double n = d / time;
			double velocity1 = n * 3600D / 1000D;

			/*
			 * 速度算出方法2
			*/

			double Tx = e.getTo().getX();
			double Tz = e.getTo().getZ();
			double Ty = e.getTo().getY();
			double Fx = e.getFrom().getX();
			double Fz = e.getFrom().getZ();
			double Fy = e.getFrom().getY();
			double x1 = Tx - Fx;
			double z1 = Tz - Fz;
			double y1 = Ty - Fy;
			double x2 = Math.pow(x1, 2);
			double z2 = Math.pow(z1, 2);
			double y2 = Math.pow(y1, 2);
			double leng = Math.sqrt(x2 + z2 + y2);
			double velocity2 = leng * 72000 / 1000;

			// メーターちらつき防止処理
			double speedThreshold = ((velocity1 + velocity2) / 2) * 15;
			double lowSpeedThreshold = 3;
			double gosa = velocity2 - velocity1;
			if (velocity1 <= 20 ) {
				if(!(normalize(gosa) > lowSpeedThreshold)) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +
							String.format("%.2f", velocity2) + ChatColor.RESET + " km/h" ));
				}
				data.setTime(currentTime);
				data.setLocation(x, z);
				return;
			}
			else if(velocity1 > 20) {
				if(normalize(gosa) > speedThreshold) {
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
					data.setTime(currentTime);
					data.setLocation(x, z);
					return;
				}
			}
		}

		public static double getRange(double a, double b, double c){
			double d = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) + Math.pow(b, 2));
			return d;
		}

		public static double getRange(double a, double b){
			double c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
			return c;
		}

		public static double normalize(double d){
			if(d < 0){
			d = -d;
			}
			return d;
			}
}
