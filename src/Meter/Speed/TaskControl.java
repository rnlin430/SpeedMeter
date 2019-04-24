package Meter.Speed;

import org.bukkit.scheduler.BukkitRunnable;

public class TaskControl extends BukkitRunnable {
	public TaskControl(){

	}

	private int count = 500;
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		if(count == 500) {
			if(!SpeedMeter.scheduler) {
				SpeedMeter.scheduler = true;
			}
		}
		if(count < 500 && count > 1) {
			if(SpeedMeter.scheduler) {
				SpeedMeter.scheduler = false;
			}
		}
		if(count <= 1) {
			if(SpeedMeter.scheduler) {
				SpeedMeter.scheduler = false;
			}
			count = 500;
			return; // countを減算させないようにここで処理を終了
		}

	count = count - SpeedMeter.countmainasu; // countをcountmainasu減算

	}
}

