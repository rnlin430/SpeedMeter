package Meter.Speed;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class VehicleListener implements Listener {

		public VehicleListener(SpeedMeter plugin) {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
		}

	private HashMap<UUID, MoveData> list = new HashMap<UUID, MoveData>();


		@EventHandler
		public void onMove(VehicleMoveEvent e) {

			if(SpeedMeter.enablemeter == false) return;
			if(SpeedMeter.scheduler == false && (SpeedMeter.countmainasu >= 500) == false) return;
			Vehicle v = e.getVehicle();
			if(!((v instanceof Boat) || (v instanceof Minecart))) return;
			List<Entity> passengers = v.getPassengers();
			if(passengers.isEmpty() || !(passengers.get(0) instanceof Player)) return;
			Player p = (Player) passengers.get(0);
			if(!p.hasPermission("speed.meter.speedmeter.boat")) return;
		    if(!SpeedMeter.AllowedWorldNames.contains(p.getWorld().getName())) return;
			if(SpeedMeter.manager.discrete_meter_on_off.containsKey(p.getName()))
				if(!(SpeedMeter.manager.discrete_meter_on_off.get(p.getName()))) return;
			UUID uuid = p.getUniqueId();
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
			double r = n * 3600D / 1000D;


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
			double velocity = leng * 72000 / 1000;

			// メーターちらつき防止処理
			double sikiiti = ((r + velocity) / 2) * 0.3;
			double teisokusikiiti = 3;
			double gosa = velocity - r;
			if (r <= 20 ) {
				if(!(normalize(gosa) > teisokusikiiti)) {
					p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
						new TextComponent(ChatColor.AQUA + "" + ChatColor.UNDERLINE +
							String.format("%.2f", velocity) + ChatColor.RESET + " km/h" ));
				}
				data.setTime(currentTime);
				data.setLocation(x, z);
				return;
			}
			else if(r > 20) {
				if(normalize(gosa) > sikiiti) {
					return;
				}
				else {
					if(r < 150) {
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
								new TextComponent(ChatColor.AQUA + "" + ChatColor.UNDERLINE +
										String.format("%.2f", velocity) + ChatColor.RESET + " km/h" ));
					}
					else {
						if(velocity < 150) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									new TextComponent(ChatColor.AQUA + "" + ChatColor.UNDERLINE +
											String.format("%.2f", velocity) + ChatColor.RESET + " km/h" ));
						}
						else if(velocity >= 150) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									new TextComponent(ChatColor.RED + "" + ChatColor.UNDERLINE +
											String.format("%.2f", velocity) + ChatColor.RESET + " km/h" ));
						}
					}
					data.setTime(currentTime);
					data.setLocation(x, z);
					return;
				}
			}
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
